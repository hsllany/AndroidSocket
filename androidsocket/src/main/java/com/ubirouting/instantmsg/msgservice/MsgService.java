package com.ubirouting.instantmsg.msgservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ubirouting.instantmsg.msgs.Heartbeat;
import com.ubirouting.instantmsg.msgs.InstantMessage;
import com.ubirouting.instantmsg.msgs.MessageFactory;
import com.ubirouting.instantmsg.serialization.DefaultSerializationFactory;
import com.ubirouting.instantmsg.serialization.SerializationAbstractFactory;
import com.ubirouting.instantmsg.serialization.bytelib.PrimaryDatas;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Work as a service, receive the message from UI and send to remote server, and get response from
 * remote server.
 *
 * @author Yang Tao on 16/6/30.
 */
public abstract class MsgService extends Service {

    public static final int MSG_SEND_MESSAGE = 0x01;

    private static final String TAG = "MsgService";

    private static final float INCREMENT_HEARTBEAT_INTERVAL = (MsgServiceConfig.MAX_HEARTBEAT_TIME - MsgServiceConfig.MIN_HEARTBEAT_TIME) / 10.f;

    // to cache the message to be send, usually send by UI component.
    private final BlockingQueue<InstantMessage> sendMessagesQueue = new LinkedBlockingQueue<>();

    // to cache the message to be dispatched
    private final BlockingQueue<InstantMessage> dispatchMessagesQueue = new LinkedBlockingQueue<>();

    private SendingThread sendingThread;

    private ReadingThread readingThread;

    private DispatchThread dispatchThread;

    private Socket socket;

    // semaphore wait for send to connect
    private Semaphore readDelaySemaphore = new Semaphore(0);

    // the formal 4 byte stores the length of the message, the latter 4 byte is code which distinguish messages.
    private byte[] msgLengthBuffer = new byte[8];

    private int heartbeatTime = 0;

    private int continusHeartbeatCount = 0;

    private int readRound = 0;

    private int writeRound = 0;

    private SerializationAbstractFactory serializationAbstractFactory;

    private Messenger mMessenger = new Messenger(new MessagerHandler());

    private MsgDispatcher msgDispatcher;

    private BroadcastReceiver NetworkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private static InstantMessage processMsg(byte[] msgBytes, int code, SerializationAbstractFactory serializationAbstractFactory) {
        return MessageFactory.buildWithCode(code, msgBytes, serializationAbstractFactory);
    }

    static void debug(String format, Object... objects) {
        if (MsgServiceConfig.DEBUG_SOCKET)
            Log.d(TAG, String.format(format, objects));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(NetworkStatusReceiver, filter);

        serializationAbstractFactory = loadSerializationFactory();
        if (serializationAbstractFactory == null) {
            serializationAbstractFactory = new DefaultSerializationFactory();
        }

        msgDispatcher = loadMessageDispatcher();
        if (msgDispatcher == null) {
            msgDispatcher = new EmptyMsgDispatcher();
        }
    }

    /**
     * Invoked at {@code onCreate()} of msgService, to get the {@link MsgDispatcher} object. If return null, than a
     * default implementation {@link MsgService.EmptyMsgDispatcher} will be used, which do nothing when message
     * arrives..
     *
     * @return the MsgDispatcher
     */
    protected abstract MsgDispatcher loadMessageDispatcher();

    protected abstract SerializationAbstractFactory loadSerializationFactory();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        debug("[MSGSERVICE START");

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
     * add the instant message to the {@code sendMessageQueue}, which will be send sequentially。.
     *
     * @param instantMessage to send.
     */
    protected final void sendInstantMessage(@NonNull InstantMessage instantMessage) {
        sendMessagesQueue.offer(instantMessage);
    }


    /**
     * pack and send the instantMessage. pack the byte array with length (int) at first 4 bytes, and code second 4 byte
     *
     * @param instantMessage to send
     * @throws IOException
     */
    protected final void sendMessageToSocketStream(@NonNull InstantMessage instantMessage) throws IOException {

        byte[] bytes = serializationAbstractFactory.buildWithObject(instantMessage);
        int code = MessageFactory.codeFromMessage(instantMessage);

        byte[] actualBytes = new byte[bytes.length + 4 + 4];
        PrimaryDatas.i2b(bytes.length + 4, actualBytes, 0);
        PrimaryDatas.i2b(code, actualBytes, 4);
        System.arraycopy(bytes, 0, actualBytes, 8, bytes.length);
        debug("[SENDING]:" + instantMessage.toString() + "@" + Arrays.toString(actualBytes));
        SocketUtils.sendBytes(socket.getOutputStream(), actualBytes);
    }

    /**
     * try to reconnect to remote server.
     *
     * @return
     */
    private boolean reconnect() {
        while (true) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (connect())
                    return true;
            }

            // Test the timeout status of the message. If timeout happens, will send it back to Client process.
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
                return false;
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

    protected void processMsgBeforeDispatch(InstantMessage instantMessage) {

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

    /**
     * Default MsgDispatcher, but doing nothing. Just in case of the Null pointer of msgDispatcher test.
     */
    private static class EmptyMsgDispatcher extends MsgDispatcher {

        @Override
        void dispatch(InstantMessage instantMessage) {
            // empty
        }
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
                        // read the first 8 byte to get the length of the instantMessage, and the instantMessage code
                        SocketUtils.readBytes(socket.getInputStream(), msgLengthBuffer, 8);
                        int msgLength = PrimaryDatas.b2i(msgLengthBuffer, 0);
                        int msgCode = PrimaryDatas.b2i(msgLengthBuffer, 4);

                        byte[] msgBuffer = new byte[PrimaryDatas.b2i(msgLengthBuffer, 0)];

                        // msgLength contains the 4 byte of msgCode
                        SocketUtils.readBytes(socket.getInputStream(), msgBuffer, msgLength - 4);
                        InstantMessage instantMessage = processMsg(msgBuffer, msgCode, serializationAbstractFactory);
                        if (instantMessage != null) {
                            debug("[RECEIVING]:" + instantMessage.toString());
                            dispatchMessagesQueue.put(instantMessage);
                        } else {
                            Log.e(TAG, "unknown msgCode " + msgCode);
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
                        processMsgBeforeDispatch(msg);
                        FindableDispatcher.getInstance().dispatch(msg);
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
                    if (reconnect()) {
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
    }

    /**
     * receive message from Client Process
     */
    private class MessagerHandler extends Handler {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SEND_MESSAGE:
                    if (msg.obj != null && msg.obj instanceof InstantMessage) {
                        FindableDispatcher.getInstance().register(msg.replyTo);
                        MsgService.this.sendInstantMessage((InstantMessage) (msg.obj));

                    } else
                        Log.e(TAG, "msg contains no InstantMessage");
                default:
                    super.handleMessage(msg);
            }

        }
    }


}
