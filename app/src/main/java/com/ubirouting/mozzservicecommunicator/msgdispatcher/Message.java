package com.ubirouting.mozzservicecommunicator.msgdispatcher;

/**
 * @author Yang Tao on 16/6/20.
 */
public class Message {

    MessageId mId;

    public void generateMessageId(Findable findable) {
        mId = new MessageId(findable);
    }

    @Override
    public String toString() {
        return "[message:id=" + mId.toString() + "]";
    }

    public MessageId getMessageId() {
        return mId;
    }


}
