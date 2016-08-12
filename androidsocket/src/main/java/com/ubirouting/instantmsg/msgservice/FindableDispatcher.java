package com.ubirouting.instantmsg.msgservice;

import android.os.Messenger;
import android.os.RemoteException;

import com.ubirouting.instantmsg.msgdispatcher.Findable;
import com.ubirouting.instantmsg.msgdispatcher.WeakList;
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
    private final WeakHashMap<Messenger, Object> sFindables = new WeakHashMap<>();
    private final Map<Class<? extends InstantMessage>, WeakList<Findable>> sTypeFindables = new HashMap<>();

    private FindableDispatcher() {

    }

    public static FindableDispatcher getInstance() {
        synchronized (FindableDispatcher.class) {
            if (instance == null)
                instance = new FindableDispatcher();
        }

        return instance;
    }

    public synchronized void register(Messenger activityMessenger) {
        synchronized (sFindables) {
            sFindables.put(activityMessenger, this);
        }
    }

    @Deprecated
    public void register(Findable activity, Class<? extends InstantMessage> messageType) {
        synchronized (sTypeFindables) {
            WeakList<Findable> list = sTypeFindables.get(messageType);
            if (list == null) {
                list = new WeakList<>();
                sTypeFindables.put(messageType, list);
            }


            list.add(activity);
        }
    }

    @Override
    public void dispatch(InstantMessage instantMessage) {

        Findable target = null;
        if (instantMessage instanceof DispatchMessage) {
            DispatchMessage msg = (DispatchMessage) instantMessage;
            synchronized (sFindables) {
                Iterator<Map.Entry<Messenger, Object>> itr = sFindables.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<Messenger, Object> entry = itr.next();
                    Messenger messenger = entry.getKey();

                    android.os.Message dispatchMessage = android.os.Message.obtain(null, 1, instantMessage);
                    try {
                        messenger.send(dispatchMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


//                    if (findable.getFindableId() == msg.getMessageId().getUIId()) {
//                        if (findable.hasBeenDestroyed()) {
//                            itr.remove();
//                        } else {
//
//                            Log.d("MessageService", "active Findable" + findable.toString());
//                            findable.execute(msg);
//                            target = findable;
//                        }
//
//                        break;
//                    }
                }
            }
        }

        synchronized (sTypeFindables) {
            WeakList<Findable> activityWeakList = sTypeFindables.get(instantMessage.getClass());
            if (activityWeakList != null) {
                for (int i = 0; i < activityWeakList.size(); i++) {
                    Findable activity = activityWeakList.get(i);
                    if (activity != null) {


                        if (target != null && target == activity) {

                        } else {
                            if (activity.hasBeenDestroyed())
                                activityWeakList.remove(i);
                            else {
                                activity.execute(instantMessage);
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
}
