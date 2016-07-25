package com.ubirouting.instantmsg.msgdispatcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import com.ubirouting.instantmsg.msgs.DispatchMessage;
import com.ubirouting.instantmsg.msgs.Message;
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
    private final Map<Class<? extends Message>, MessageConsumeListener> mTypeList = new ArrayMap<>();
    private final long id = System.currentTimeMillis() + hashCode();
    private MsgService mService;
    private volatile boolean isBound;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Intent intent = new Intent(this, MsgService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public final void sendMessage(DispatchMessage msg, MessageConsumeListener l) {
        if (isBound) {
            synchronized (mListenerList) {
                mListenerList.put(msg.getMessageId(), l);
            }

            FindableDispatcher.getInstance().register(this, msg);
            mService.sendMessage(msg);
        }
    }

    public final void registerListener(Class<? extends Message> msgClass, MessageConsumeListener l) {
        synchronized (mTypeList) {
            mTypeList.put(msgClass, l);
        }

        FindableDispatcher.getInstance().register(this, msgClass);
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
    public final void execute(final Message msg) {

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
            Iterator<Map.Entry<Class<? extends Message>, MessageConsumeListener>> itr2 = mTypeList.entrySet().iterator();
            while (itr2.hasNext()) {
                final Map.Entry<Class<? extends Message>, MessageConsumeListener> entry = itr2.next();

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
        MsgService.MessageBinder binder = (MsgService.MessageBinder) service;
        mService = binder.getService();
        isBound = true;
    }

    @Override
    public final void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }
}
