package com.ubirouting.mozzservicecommunicator.msgdispatcher;

/**
 * @author Yang Tao on 16/6/21.
 */
public interface MessageConsumeListener {
    void consume(Message msg);
}
