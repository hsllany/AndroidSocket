package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.msgdispatcher.Findable;

public abstract class DispatchMessage extends Message {

    private MessageId mId;

    public DispatchMessage(Findable findable) {
        super();
        mId = new MessageId(findable);
    }

    public DispatchMessage(byte[] rawBytes) {
        super(rawBytes);
    }

    @Override
    public String toString() {
        return "[message:id=" + mId.toString() + "]";
    }

    public MessageId getMessageId() {
        return mId;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DispatchMessage && ((DispatchMessage) o).mId.equals(mId);

    }
}
