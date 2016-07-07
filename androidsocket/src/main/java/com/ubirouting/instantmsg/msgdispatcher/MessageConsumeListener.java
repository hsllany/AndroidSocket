package com.ubirouting.instantmsg.msgdispatcher;

import com.ubirouting.instantmsg.msgs.DispatchableMessage;

/**
 * @author Yang Tao on 16/6/21.
 */
public interface MessageConsumeListener {
    void consume(DispatchableMessage msg);
}
