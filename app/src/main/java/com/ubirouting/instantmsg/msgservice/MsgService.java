package com.ubirouting.instantmsg.msgservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ubirouting.instantmsg.msgdispatcher.FindableDispatcher;
import com.ubirouting.instantmsg.msgs.Heartbeat;
import com.ubirouting.instantmsg.msgs.Message;
import com.ubirouting.instantmsg.msgs.MessageImp;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Yang Tao on 16/6/30.
 */
public class MsgService extends Service {

    private BlockingQueue<MessageImp> sendMessages;

    private BlockingQueue<MessageImp> dispatchMessages;

    private SendingThread sendingThread;
    private ReadingThread readingThread;
    private DispatchThread dispatchThread;

    private Socket socket;

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

    private class SendingThread extends Thread {
        private volatile boolean isRunFlag = true;

        @Override
        public void run() {
            while (isRunFlag) {
                MessageImp sendMsg = null;

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
                    e.printStackTrace();
                }


            }
        }
    }

    protected void sendMessage(Message message) throws IOException {

    }

    protected void reconnect() {
        while (true) {
            if (connect())
                break;

            try {
                wait(200);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    protected boolean connect() {
        return true;
    }


    private class ReadingThread extends Thread {
        private volatile boolean isRunFlag = true;

        @Override
        public void run() {
            super.run();
        }
    }

    private class DispatchThread extends Thread {
        private volatile boolean isRunFlag = true;

        @Override
        public void run() {
            while (isRunFlag) {
                try {
                    MessageImp dispatchMsg = null;
                    dispatchMsg = dispatchMessages.take();

                    if (dispatchMsg != null)
                        FindableDispatcher.getInstance().dispatch(dispatchMsg);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }


}
