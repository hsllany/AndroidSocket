package com.ubirouting.instantmsg.msgdispatcher;

import android.util.Log;

import com.ubirouting.instantmsg.msgs.DispatchableMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Yang Tao on 16/6/20.
 */
public class FindableDispatcher {

    private static FindableDispatcher instance = null;

    private FindableDispatcher() {

    }

    public static FindableDispatcher getInstance() {
        synchronized (FindableDispatcher.class) {
            if (instance == null)
                instance = new FindableDispatcher();
        }

        return instance;
    }

    private final WeakHashMap<Findable, Object> sFindables = new WeakHashMap<>();

    private final Map<Class<? extends DispatchableMessage>, WeakList<Findable>> sTypeFindables = new HashMap<>();

    public synchronized void register(Findable activity, DispatchableMessage message) {
        synchronized (sFindables) {
            sFindables.put(activity, this);
        }
    }

    public void register(Findable activity, Class<? extends DispatchableMessage> messageType) {
        synchronized (sTypeFindables) {
            WeakList<Findable> list = sTypeFindables.get(messageType);
            if (list == null) {
                list = new WeakList<>();
                sTypeFindables.put(messageType, list);
            }


            list.add(activity);
        }
    }

    public void dispatch(DispatchableMessage message) {

        Findable target = null;
        synchronized (sFindables) {
            Iterator<Map.Entry<Findable, Object>> itr = sFindables.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<Findable, Object> entry = itr.next();
                Findable findable = entry.getKey();


                if (findable.getFindableId() == message.getMessageId().getUIId()) {
                    if (findable.hasBeenDestroyed()) {
                        itr.remove();
                    } else {

                        Log.d("MessageService", "active Findable" + findable.toString());
                        findable.execute(message);
                        target = findable;
                    }

                    break;
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
