package com.ubirouting.instantmsg.basic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.ubirouting.instantmsg.msgs.InstantMessage;
import com.ubirouting.instantmsg.msgs.MessageId;
import com.ubirouting.instantmsg.msgservice.MsgService;
import com.ubirouting.instantmsg.msgservice.Transaction;
import com.ubirouting.instantmsg.serialization.AbstractSerializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Yang Tao on 16/6/20.
 */
public abstract class FindableActivity extends AppCompatActivity implements Findable, ServiceConnection {

    private final Map<MessageId, MessageConsumeListener> mListenerList = new HashMap<>();
    private final Map<Class<? extends InstantMessage>, MessageConsumeListener> mTypeList = new ArrayMap<>();
    private HandlerThread executeThread = new HandlerThread("ExcuteDispatchThread");
    private Messenger mMessenger;
    private Messenger mServiceBinder;
    private volatile boolean isBound;

    private AbstractSerializer serializer;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        executeThread.start();
        mMessenger = new Messenger(new MessengerHandler(executeThread.getLooper()));
        serializer = Injection.provideSerializer();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executeThread.start();
        mMessenger = new Messenger(new MessengerHandler(executeThread.getLooper()));
        serializer = Injection.provideSerializer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindMsgService();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executeThread.quit();
    }

    private void bindMsgService() {
        Intent intent = new Intent(this, MsgService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public final void sendMessage(InstantMessage msg, MessageConsumeListener l) {
        if (isBound) {
            synchronized (mListenerList) {
                mListenerList.put(msg.getMessageId(), l);
            }

            Message handlerMessage = Transaction.getMessage(msg, mMessenger, this, MsgService.MSG_SEND_MESSAGE, serializer);

            try {
                mServiceBinder.send(handlerMessage);
            } catch (RemoteException e) {
                bindMsgService();
            }
        }
    }

    @Override
    public final void registerMessageBroadcastListener(Class<? extends InstantMessage> msgClass, MessageConsumeListener l) {
        synchronized (mTypeList) {
            mTypeList.put(msgClass, l);
        }

    }

    @Override
    public final void onGetInstantMessageReply(final InstantMessage msg) {

        if (msg instanceof InstantMessage) {
            InstantMessage msgDis = msg;
            synchronized (mListenerList) {
                Iterator<Map.Entry<MessageId, MessageConsumeListener>> itr = mListenerList.entrySet().iterator();
                while (itr.hasNext()) {
                    final Map.Entry<MessageId, MessageConsumeListener> entry = itr.next();
                    if (entry.getKey().equals(msgDis.getMessageId())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                entry.getValue().consume(msg);

                            }
                        });
                        itr.remove();
                        return;
                    }
                }
            }
        }


        synchronized (mTypeList) {
            Iterator<Map.Entry<Class<? extends InstantMessage>, MessageConsumeListener>> itr2 = mTypeList.entrySet().iterator();
            while (itr2.hasNext()) {
                final Map.Entry<Class<? extends InstantMessage>, MessageConsumeListener> entry = itr2.next();

                if (entry.getKey().equals(msg.getClass())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            entry.getValue().consume(msg);
                        }
                    });
                    return;
                }
            }
        }
    }

    @Override
    public final void onServiceConnected(ComponentName name, IBinder service) {
        mServiceBinder = new Messenger(service);
        isBound = true;
    }

    @Override
    public final void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    private class MessengerHandler extends Handler {

        public MessengerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            InstantMessage dispatchInstantMessage = Transaction.getInstantMessage(msg, serializer);
            onGetInstantMessageReply(dispatchInstantMessage);
        }
    }


}
