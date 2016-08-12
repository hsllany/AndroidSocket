package com.ubirouting.instantmsg.msgdispatcher;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/20.
 */
public interface Findable {

    long getFindableId();

    void execute(InstantMessage msg);

    boolean hasBeenDestroyed();
}
