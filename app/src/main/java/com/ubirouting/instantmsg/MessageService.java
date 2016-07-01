package com.ubirouting.instantmsg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ubirouting.instantmsg.msgdispatcher.FindableDispatcher;
import com.ubirouting.instantmsg.msgs.MessageImp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yang Tao on 16/6/20.
 */
public class MessageService extends Service {

    static MessageService instance = null;

    public static MessageService getInstance() {
        return instance;
    }

    BlockingQueue<MessageImp> messageQueue = new LinkedBlockingQueue<>(23);
    DispacherThread mDispatcherThread = new DispacherThread();

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if (!mDispatcherThread.isAlive())
            mDispatcherThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendMessage(MessageImp message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    class DispacherThread extends Thread {

        @Override
        public void run() {
            while (true) {

                try {

                    MessageImp msg = null;

                    msg = messageQueue.take();
                    Log.d("MessageService", "get MessageImp");


                    if (msg != null)
                        FindableDispatcher.getInstance().dispatch(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }
}
