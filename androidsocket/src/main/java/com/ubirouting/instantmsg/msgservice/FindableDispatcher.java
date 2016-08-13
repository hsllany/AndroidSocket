package com.ubirouting.instantmsg.msgservice;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.ubirouting.instantmsg.basic.Findable;
import com.ubirouting.instantmsg.basic.WeakList;
import com.ubirouting.instantmsg.msgs.DispatchMessage;
import com.ubirouting.instantmsg.msgs.InstantMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Yang Tao on 16/6/20.
 */
public class FindableDispatcher extends MsgDispatcher {

    private static FindableDispatcher instance = null;
    private final WeakHashMap<MessengerWithId, Object> sFindables = new WeakHashMap<>();
    private final Map<Class<? extends InstantMessage>, WeakList<Messenger>> sTypeFindables = new HashMap<>();

    private FindableDispatcher() {

    }

    public static FindableDispatcher getInstance() {
        synchronized (FindableDispatcher.class) {
            if (instance == null)
                instance = new FindableDispatcher();
        }

        return instance;
    }

    public synchronized void register(Messenger activityMessenger, Findable findable) {
        synchronized (sFindables) {
            sFindables.put(new MessengerWithId(activityMessenger, findable.getFindableId()), this);
        }
    }

    public void register(Messenger activityMessenger, Class<? extends InstantMessage> messageType) {
        synchronized (sTypeFindables) {
            WeakList<Messenger> list = sTypeFindables.get(messageType);
            if (list == null) {
                list = new WeakList<>();
                sTypeFindables.put(messageType, list);
            }


            list.add(activityMessenger);
        }
    }

    @Override
    public void dispatch(InstantMessage instantMessage) {

        Messenger target = null;
        if (instantMessage instanceof DispatchMessage) {
            DispatchMessage msg = (DispatchMessage) instantMessage;
            synchronized (sFindables) {
                Iterator<Map.Entry<MessengerWithId, Object>> itr = sFindables.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<MessengerWithId, Object> entry = itr.next();
                    MessengerWithId messengerWithId = entry.getKey();

                    if (messengerWithId.getId() == msg.getMessageId().getUIId()) {
                        Message dispatchMessage = Message.obtain();
                        dispatchMessage.what = MsgService.MSG_RESPONSE_MESSAGE;
                        dispatchMessage.obj = instantMessage;
                        target = messengerWithId.getMessenger();
                        try {
                            messengerWithId.getMessenger().send(dispatchMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }
        }

        synchronized (sTypeFindables) {
            WeakList<Messenger> activityWeakList = sTypeFindables.get(instantMessage.getClass());
            if (activityWeakList != null) {
                for (int i = 0; i < activityWeakList.size(); i++) {
                    Messenger activity = activityWeakList.get(i);
                    if (activity != null) {
                        if (target != null && target == activity) {

                        } else {
                            Message msg = Message.obtain();
                            msg.obj = instantMessage;
                            msg.what = MsgService.MSG_RESPONSE_MESSAGE;
                            try {
                                activity.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }

    }

    public synchronized void clear() {
        synchronized (sFindables) {
            sFindables.clear();
        }

        synchronized (sTypeFindables) {
            sTypeFindables.clear();
        }
    }

    private static class MessengerWithId {
        private Messenger messenger;
        private long id;

        MessengerWithId(Messenger mesger, long id) {
            this.messenger = mesger;
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public Messenger getMessenger() {
            return messenger;
        }
    }
}
