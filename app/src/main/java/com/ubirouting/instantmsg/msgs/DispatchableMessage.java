package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.msgdispatcher.Findable;

public abstract class DispatchableMessage implements Message {

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

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DispatchableMessage && ((DispatchableMessage) o).mId.equals(mId);

    }
}
