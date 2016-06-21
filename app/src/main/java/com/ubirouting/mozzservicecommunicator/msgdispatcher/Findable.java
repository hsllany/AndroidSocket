package com.ubirouting.mozzservicecommunicator.msgdispatcher;

/**
 * @author Yang Tao on 16/6/20.
 */
public interface Findable {

    long getFindableId();

    void execute(Message msg);

    boolean hasBeenDestroyed();
}
