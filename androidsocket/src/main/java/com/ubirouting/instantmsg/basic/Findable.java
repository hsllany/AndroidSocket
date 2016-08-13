package com.ubirouting.instantmsg.basic;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/6/20.
 */
public interface Findable {

    int getFindableId();

    void execute(InstantMessage msg);

    boolean hasBeenDestroyed();
}
