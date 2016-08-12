package com.ubirouting.instantmsg.msgservice;

import com.ubirouting.instantmsg.msgs.InstantMessage;

/**
 * @author Yang Tao on 16/8/12.
 */
public abstract class MsgDispatcher {

    abstract void dispatch(InstantMessage instantMessage);
}
