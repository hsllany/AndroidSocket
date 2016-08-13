package com.ubirouting.instantmsg.basic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.ubirouting.instantmsg.msgs.DispatchMessage;
import com.ubirouting.instantmsg.msgs.InstantMessage;
import com.ubirouting.instantmsg.msgs.MessageId;
import com.ubirouting.instantmsg.msgservice.MsgService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Yang Tao on 16/6/20.
 */
public abstract class FindableActivity extends AppCompatActivity implements Findable, ServiceConnection {

    private final Map<MessageId, MessageConsumeListener> mListenerList = new HashMap<>();
    private final Map<Class<? extends InstantMessage>, MessageConsumeListener> mTypeList = new ArrayMap<>();
    private final long id = System.currentTimeMillis() + hashCode();
    private final Messenger mMessenger = new Messenger(new MessengerHandler());
    private Messenger mServiceBinder;
    private volatile boolean isBound;

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

    private void bindMsgService() {
        Intent intent = new Intent(this, MsgService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public final void sendMessage(DispatchMessage msg, MessageConsumeListener l) {
        if (isBound) {
            synchronized (mListenerList) {
                mListenerList.put(msg.getMessageId(), l);
            }

            android.os.Message handlerMessage = android.os.Message.obtain(null, MsgService.MSG_SEND_MESSAGE, msg);
            handlerMessage.replyTo = mMessenger;
            try {
                mServiceBinder.send(handlerMessage);
            } catch (RemoteException e) {
                bindMsgService();
            }
        }
    }

    public final void registerListener(Class<? extends InstantMessage> msgClass, MessageConsumeListener l) {
        synchronized (mTypeList) {
            mTypeList.put(msgClass, l);
        }

    }

    @Override
    public final long getFindableId() {
        return id;
    }

    @Override
    public final boolean hasBeenDestroyed() {
        return this.isDestroyed();
    }

    @Override
    public final void execute(final InstantMessage msg) {

        if (msg instanceof DispatchMessage) {
            DispatchMessage msgDis = (DispatchMessage) msg;
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
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.obj != null && msg.obj instanceof InstantMessage) {
                InstantMessage dispatchInstantMessage = (InstantMessage) msg.obj;

            }
        }
    }
}
