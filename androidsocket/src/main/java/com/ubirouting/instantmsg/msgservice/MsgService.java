package com.ubirouting.instantmsg.msgservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ubirouting.instantmsg.msgs.Heartbeat;
import com.ubirouting.instantmsg.msgs.InstantMessage;
import com.ubirouting.instantmsg.msgs.MessageId;
import com.ubirouting.instantmsg.serialization.AbstractSerializer;
import com.ubirouting.instantmsg.serialization.bytelib.PrimaryDatas;
import com.ubirouting.instantmsg.utils.$Checkr;
import com.ubirouting.instantmsg.basic.Injection;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.ubirouting.instantmsg.msgservice.Transaction.*;

/**
 * Work as a service, receive the instantMessage from UI and send to remote server, and get response from
 * remote server.
 *
 * @author Yang Tao on 16/6/30.
 */
public class MsgService extends Service {

    public static final int MSG_SEND_MESSAGE = 0x01;

    public static final int MSG_RESPONSE_MESSAGE = 0x02;

    private static final String TAG = "MsgService";

    private static final float INCREMENT_HEARTBEAT_INTERVAL = (MsgServiceConfig.MAX_HEARTBEAT_TIME - MsgServiceConfig.MIN_HEARTBEAT_TIME) / 10.f;

    // to cache the instantMessage to be send, usually send by UI component.
    private final BlockingQueue<InstantMessage> sendMessagesQueue = new LinkedBlockingQueue<>();

    // to cache the instantMessage to be dispatched
    private final BlockingQueue<InstantMessage> dispatchMessagesQueue = new LinkedBlockingQueue<>();

    private SendingThread sendingThread;

    private ReadingThread readingThread;

    private DispatchThread dispatchThread;

    private Socket socket;

    // semaphore wait for send to connect
    private Semaphore readDelaySemaphore = new Semaphore(0);

    // the formal 4 byte stores the length of the instantMessage, the latter 4 byte is code which distinguish messages.
    private byte[] msgLengthBuffer = new byte[8];

    private int heartbeatTime = 0;

    private int continusHeartbeatCount = 0;

    private int readRound = 0;

    private int writeRound = 0;

    private AbstractSerializer abstractSerializer;

    private Messenger mMessenger;

    private FindableDispatcher msgDispatcher;

    private BroadcastReceiver NetworkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private HandlerThread messageThread = new HandlerThread("MessageThread");

    public MsgService() {
        super();
    }

    private static InstantMessage parseMsgFromRawBytes(byte[] msgBytes, AbstractSerializer abstractSerializer) {
        return (InstantMessage) abstractSerializer.buildViaBytes(msgBytes);
    }

    protected static void debug(String format, Object... objects) {
        if (MsgServiceConfig.DEBUG_SOCKET)
            Log.d(TAG, String.format(format, objects));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        messageThread.start();
        mMessenger = new Messenger(new MessageHandler(messageThread.getLooper()));

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(NetworkStatusReceiver, filter);
        msgDispatcher = new FindableDispatcher();
        abstractSerializer = Injection.provideSerializer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        debug("[MSG SERVICE START");

        if (sendingThread == null || !sendingThread.isAlive()) {
            sendingThread = new SendingThread();
            sendingThread.start();
        }

        if (readingThread == null || !readingThread.isAlive()) {
            readingThread = new ReadingThread();
            readingThread.start();
        }

        if (dispatchThread == null || !dispatchThread.isAlive()) {
            dispatchThread = new DispatchThread();
            dispatchThread.start();
        }


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sendingThread != null) {
            sendingThread.isRunFlag = false;
            sendingThread.interrupt();
        }

        if (readingThread != null) {
            readingThread.isRunFlag = false;
            readingThread.interrupt();
        }

        if (dispatchThread != null) {
            dispatchThread.isRunFlag = false;
            dispatchThread.interrupt();
        }

        unregisterReceiver(NetworkStatusReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * add the instant instantMessage to the {@code sendMessageQueue}, which will be send sequentially。.
     *
     * @param instantMessage to send.
     */
    protected final void addToSendPendingQueue(@NonNull InstantMessage instantMessage) {
        sendMessagesQueue.offer(instantMessage);
    }


    /**
     * pack and send the instantMessage. pack the byte array with length (int) at first 4 bytes
     *
     * @param instantMessage to send
     * @throws IOException
     */
    protected final void sendMessageToSocketStream(@NonNull InstantMessage instantMessage) throws IOException {
        $Checkr.notNull(instantMessage, "instant message can't be null");
        byte[] bytes = abstractSerializer.buildWithObject(instantMessage);
        byte[] actualBytes = new byte[bytes.length + 4];
        PrimaryDatas.i2b(bytes.length, actualBytes, 0);
        System.arraycopy(bytes, 0, actualBytes, 4, bytes.length);
        SocketUtils.sendBytes(socket.getOutputStream(), actualBytes);
    }

    /**
     * try to reconnect to remote server.
     *
     * @return true if connected
     */
    private void reconnect() {
        while (true) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (connect())
                    return;
            }

            // Test the timeout status of the instantMessage. If timeout happens, will send it back to Client process.
            synchronized (sendMessagesQueue) {
                Iterator<InstantMessage> itr = sendMessagesQueue.iterator();
                while (itr.hasNext()) {
                    InstantMessage msg = itr.next();

                    if (System.currentTimeMillis() - msg.getTimestamp() > MsgServiceConfig.MESSAGE_SEND_TIME_OUT) {
                        itr.remove();
                        msg.updateStatus(InstantMessage.STATUS_FAILED);
                        dispatchMessagesQueue.offer(msg);
                    }
                }
            }

            try {
                synchronized (this) {
                    wait(MsgServiceConfig.CONNECT_TRY_TIMEOUT);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private boolean connect() {
        try {
            debug("[CONNECTING..]");
            socket = new Socket(MsgServiceConfig.HOST, MsgServiceConfig.PORT);
            socket.setSoTimeout(MsgServiceConfig.SOCKET_TIME_OUT);
            heartbeatTime = MsgServiceConfig.MIN_HEARTBEAT_TIME;
            continusHeartbeatCount = 0;
            debug("[CONNECTING SUCCESSFULLY");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    protected void hookBeforeDispatch(InstantMessage instantMessage) {
    }

    protected void handleNoFindableMessage(InstantMessage message) {
    }


    private void calculateHeartbeatTime() {
        heartbeatTime = (int) (MsgServiceConfig.MIN_HEARTBEAT_TIME + INCREMENT_HEARTBEAT_INTERVAL * continusHeartbeatCount);
    }

    private void closeSocket() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket = null;
    }

    private class ReadingThread extends Thread {
        private volatile boolean isRunFlag = true;

        @Override
        public void run() {
            while (isRunFlag) {
                try {
                    // wait for the sendingThread to reconnect
                    readDelaySemaphore.acquire();
                } catch (InterruptedException e) {
                    return;
                }

                debug("START TO READ");

                while (true) {
                    try {
                        // read the first 4 byte to get the length of the instantMessage
                        SocketUtils.readBytes(socket.getInputStream(), msgLengthBuffer, 4);
                        int msgLength = PrimaryDatas.b2i(msgLengthBuffer, 0);
                        byte[] msgBuffer = new byte[PrimaryDatas.b2i(msgLengthBuffer, 0)];

                        SocketUtils.readBytes(socket.getInputStream(), msgBuffer, msgLength);
                        InstantMessage instantMessage = parseMsgFromRawBytes(msgBuffer, abstractSerializer);
                        if (instantMessage != null) {
                            debug("[RECEIVING]:" + instantMessage.toString());
                            dispatchMessagesQueue.put(instantMessage);
                        } else {

                        }

                        readRound++;
                    } catch (IOException | InterruptedException e) {
                        closeSocket();
                        e.printStackTrace();
                        // IO Exception means connection failed
                        sendingThread.interrupt();

                        break;
                    }
                }
            }
        }
    }

    private class DispatchThread extends Thread {
        private volatile boolean isRunFlag = true;

        @Override
        public void run() {
            while (isRunFlag) {
                try {
                    InstantMessage msg = dispatchMessagesQueue.take();

                    if (msg != null) {
                        hookBeforeDispatch(msg);
                        if (msg.getMessageId().getUIId() == MessageId.NO_FINDABLE) {
                            handleNoFindableMessage(msg);
                        } else {
                            msgDispatcher.dispatch(msg, Injection.provideSerializer());
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private class SendingThread extends Thread {
        private volatile boolean isRunFlag = true;

        @Override
        public void run() {
            debug("[WAIT FOR MESSAGE TO SEND]");
            while (isRunFlag) {
                debug("[SEND ROUND]");
                if (socket == null || !socket.isConnected()) {

                    // will block until connection has been established
                    reconnect();

                    readDelaySemaphore.release();

                    while (!Thread.interrupted()) {
                        InstantMessage sendMsg;

                        try {
                            sendMsg = sendMessagesQueue.poll(heartbeatTime / 1000, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            closeSocket();
                            break;
                        }

                        try {
                            if (sendMsg != null) {
                                sendMessageToSocketStream(sendMsg);
                                continusHeartbeatCount = 0;
                            } else {
                                sendMessageToSocketStream(new Heartbeat());
                                continusHeartbeatCount++;
                                if (continusHeartbeatCount > 10)
                                    continusHeartbeatCount = 10;

                            }

                            calculateHeartbeatTime();
                            writeRound++;
                        } catch (IOException e) {
                            closeSocket();
                            break;
                        }
                    }

                }
            }
        }
    }

    /**
     * receive instantMessage from Client Process
     */
    private class MessageHandler extends Handler {

        MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SEND_MESSAGE:
                    int findableId = msg.arg1;
                    msgDispatcher.register(msg.replyTo, findableId);
                    addToSendPendingQueue(getInstantMessage(msg, Injection.provideSerializer()));
                default:
                    super.handleMessage(msg);
            }

        }
    }


}
