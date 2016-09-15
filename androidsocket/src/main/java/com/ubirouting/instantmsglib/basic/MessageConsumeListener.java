package com.ubirouting.instantmsglib.basic;

import com.ubirouting.instantmsglib.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/21.
 */
public interface MessageConsumeListener {
    void consume(InstantMessage msg);
}
