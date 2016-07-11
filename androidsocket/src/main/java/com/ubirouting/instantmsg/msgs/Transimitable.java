package com.ubirouting.instantmsg.msgs;

/**
 * @author Yang Tao on 16/7/11.
 */
public interface Transimitable {

    /**
     * The byte array which can be transferred through socket.
     *
     * @return
     */
    byte[] bytes();
}
