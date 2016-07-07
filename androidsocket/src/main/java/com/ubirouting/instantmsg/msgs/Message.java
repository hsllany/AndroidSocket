package com.ubirouting.instantmsg.msgs;

/**
 * @author Yang Tao on 16/6/30.
 */
public interface Message {

    /**
     * The byte array which can be transferred through socket.
     *
     * @return
     */
    byte[] bytes();
}
