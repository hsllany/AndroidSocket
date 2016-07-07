package com.ubirouting.instantmsg.msgservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ubirouting.instantmsg.msgdispatcher.FindableDispatcher;
import com.ubirouting.instantmsg.msgdispatcher.PrimaryDatas;
import com.ubirouting.instantmsg.msgs.DispatchableMessage;
import com.ubirouting.instantmsg.msgs.Heartbeat;
import com.ubirouting.instantmsg.msgs.Message;
import com.ubirouting.instantmsg.msgs.MessageFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Yang Tao on 16/6/30.
 */
public class MsgService extends Service {

    public static String HOST = "192.168.1.107";
    public static int PORT = 10002;

    private static final String TAG = "MsgService";

    private BlockingQueue<DispatchableMessage> sendMessages;

    private BlockingQueue<Message> dispatchMessages;

    private SendingThread sendingThread;
    private ReadingThread readingThread;
    private DispatchThread dispatchThread;

    private Socket socket;

    // semaphore wait for send to connect
    private Semaphore readDelaySemaphore = new Semaphore(0);

    // the formal 4 byte stores the length of the message, the latter 4 byte is code which distinguish messages.
    private byte[] msgLengthBuffer = new byte[8];

    @Override
    public void onCreate() {
        super.onCreate();

        sendMessages = new LinkedBlockingQueue<>();
        dispatchMessages = new LinkedBlockingQueue<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendingThread = new SendingThread();
        readingThread = new ReadingThread();
        dispatchThread = new DispatchThread();

        sendingThread.start();
        readingThread.start();
        dispatchThread.start();

        return super.onStartCommand(intent, flags, startId);
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected void sendMessage(Message message) throws IOException {
        byte[] bytes = message.bytes();
        int code = MessageFactory.codeFromMessage(message);
    }


    protected final boolean reconnect() {
        while (true) {
            if (connect())
                return true;

            try {
                synchronized (this) {
                    wait(200);
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
    }

    protected final boolean connect() {
        try {
            socket = new Socket(HOST, PORT);
            return true;
        } catch (IOException e) {
            return false;
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

                while (true) {
                    try {
                        // read the first 8 byte to get the length of the message, and the message code
                        readBytes(socket.getInputStream(), msgLengthBuffer, 8);
                        int msgLength = PrimaryDatas.b2i(msgLengthBuffer, 0);
                        int msgCode = PrimaryDatas.b2i(msgLengthBuffer, 4);

                        byte[] msgBuffer = new byte[PrimaryDatas.b2i(msgLengthBuffer, 0)];
                        readBytes(socket.getInputStream(), msgBuffer, msgLength);
                        dispatchMessages.put(processMsg(msgBuffer, msgCode));
                    } catch (IOException | InterruptedException e) {
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
                    Message dispatchMsg = dispatchMessages.take();

                    if (dispatchMsg != null && dispatchMsg instanceof DispatchableMessage)
                        FindableDispatcher.getInstance().dispatch((DispatchableMessage) dispatchMsg);
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
            while (isRunFlag) {

                if (socket == null || !socket.isConnected()) {
                    if (reconnect()) {
                        readDelaySemaphore.release();

                        while (true) {
                            DispatchableMessage sendMsg = null;

                            try {
                                sendMsg = sendMessages.poll(20000, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                break;
                            }

                            try {
                                if (sendMsg != null) {
                                    sendMessage(sendMsg);
                                } else {
                                    sendMessage(new Heartbeat());
                                }
                            } catch (IOException e) {
                                break;
                            }
                        }
                    }


                }
            }
        }
    }

    private Message processMsg(byte[] msgBytes, int code) {
        return MessageFactory.buildWithCode(code, msgBytes);
    }

    private static void readBytes(InputStream in, byte[] buffer, int readLength) throws IOException {
        int start = 0;
        int length = readLength;
        while (start < readLength) {
            int r = in.read(buffer, start, length);
            if (r == -1) //reach the end of the stream
                return;
            start += r;
            length -= r;
        }
    }


}
