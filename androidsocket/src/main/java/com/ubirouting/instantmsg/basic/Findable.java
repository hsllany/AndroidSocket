package com.ubirouting.instantmsg.basic;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/20.
 */
public interface Findable {

    int getFindableId();

    void onGetInstantMessageReply(InstantMessage msg);

    void sendMessage(InstantMessage msg, MessageConsumeListener l);

    void registerMessageBroadcastListener(Class<? extends InstantMessage> msgClass, MessageConsumeListener l);
}
