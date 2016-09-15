package com.ubirouting.instantmsglib.msgservice;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.ubirouting.instantmsglib.basic.Findable;
import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.msgs.MessageId;
import com.ubirouting.instantmsglib.serialization.AbstractSerializer;
import com.ubirouting.instantmsglib.utils.WeakList;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Yang Tao on 16/6/20.
 */
public final class FindableDispatcher {

    private final WeakHashMap<Messenger, Integer> sFindables = new WeakHashMap<>();
    private final Map<Class<? extends InstantMessage>, WeakList<Messenger>> sTypeFindables = new HashMap<>();


    public void register(Messenger activityMessenger, int findableId) {
        synchronized (sFindables) {
            sFindables.put(activityMessenger, findableId);
        }
    }

    public void register(Messenger activityMessenger, Findable findable) {
        register(activityMessenger, findable.getFindableId());
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

    public void dispatch(InstantMessage dispatchMessage, AbstractSerializer serializer) {
        if (dispatchMessage.getMessageId().getUIId() == MessageId.NO_FINDABLE)
            return;

        Messenger target = null;
        synchronized (sFindables) {
            for (Map.Entry<Messenger, Integer> entry : sFindables.entrySet()) {
                Messenger messenger = entry.getKey();
                int findableId = entry.getValue();

                if (findableId == dispatchMessage.getMessageId().getUIId()) {
                    Message msg = Transaction.getMessage(dispatchMessage, null, null, MsgService.MSG_RESPONSE_MESSAGE, serializer);
                    target = messenger;
                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }

        synchronized (sTypeFindables) {
            WeakList<Messenger> activityWeakList = sTypeFindables.get(dispatchMessage.getClass());
            if (activityWeakList != null) {
                for (int i = 0; i < activityWeakList.size(); i++) {
                    Messenger activity = activityWeakList.get(i);
                    if (activity != null) {
                        if (target == null || target != activity) {
                            Message msg = Transaction.getMessage(dispatchMessage, null, null, MsgService.MSG_RESPONSE_MESSAGE, serializer);
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

    public void clear() {
        synchronized (sFindables) {
            sFindables.clear();
        }

        synchronized (sTypeFindables) {
            sTypeFindables.clear();
        }
    }
}
