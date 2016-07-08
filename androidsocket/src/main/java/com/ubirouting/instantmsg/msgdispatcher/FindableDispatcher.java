package com.ubirouting.instantmsg.msgdispatcher;

import android.util.Log;

import com.ubirouting.instantmsg.msgs.DispatchMessage;
import com.ubirouting.instantmsg.msgs.Message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Yang Tao on 16/6/20.
 */
public class FindableDispatcher {

    private static FindableDispatcher instance = null;
    private final WeakHashMap<Findable, Object> sFindables = new WeakHashMap<>();
    private final Map<Class<? extends DispatchMessage>, WeakList<Findable>> sTypeFindables = new HashMap<>();

    private FindableDispatcher() {

    }

    public static FindableDispatcher getInstance() {
        synchronized (FindableDispatcher.class) {
            if (instance == null)
                instance = new FindableDispatcher();
        }

        return instance;
    }

    public synchronized void register(Findable activity, DispatchMessage message) {
        synchronized (sFindables) {
            sFindables.put(activity, this);
        }
    }

    public void register(Findable activity, Class<? extends DispatchMessage> messageType) {
        synchronized (sTypeFindables) {
            WeakList<Findable> list = sTypeFindables.get(messageType);
            if (list == null) {
                list = new WeakList<>();
                sTypeFindables.put(messageType, list);
            }


            list.add(activity);
        }
    }

    public void dispatch(Message message) {

        Findable target = null;
        if (message instanceof DispatchMessage) {
            DispatchMessage msg = (DispatchMessage) message;
            synchronized (sFindables) {
                Iterator<Map.Entry<Findable, Object>> itr = sFindables.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<Findable, Object> entry = itr.next();
                    Findable findable = entry.getKey();


                    if (findable.getFindableId() == msg.getMessageId().getUIId()) {
                        if (findable.hasBeenDestroyed()) {
                            itr.remove();
                        } else {

                            Log.d("MessageService", "active Findable" + findable.toString());
                            findable.execute(msg);
                            target = findable;
                        }

                        break;
                    }
                }
            }
        }

        synchronized (sTypeFindables) {
            WeakList<Findable> activityWeakList = sTypeFindables.get(message.getClass());
            if (activityWeakList != null) {
                for (int i = 0; i < activityWeakList.size(); i++) {
                    Findable activity = activityWeakList.get(i);
                    if (activity != null) {


                        if (target != null && target == activity) {

                        } else {
                            if (activity.hasBeenDestroyed())
                                activityWeakList.remove(i);
                            else {
                                activity.execute(message);
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