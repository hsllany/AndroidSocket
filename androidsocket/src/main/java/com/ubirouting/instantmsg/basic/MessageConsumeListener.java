package com.ubirouting.instantmsg.basic;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/21.
 */
public interface MessageConsumeListener {
    void consume(InstantMessage msg);
}
