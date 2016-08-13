package com.ubirouting.instantmsg.msgs;

import android.support.annotation.NonNull;

import com.ubirouting.instantmsg.basic.Findable;
import com.ubirouting.instantmsg.serialization.bytelib.ToByte;

public abstract class DispatchMessage extends InstantMessage {

    @ToByte(order = ToByte.LAST)
    private MessageId mId;


    public DispatchMessage(@NonNull Findable findable) {
        super();
        mId = new MessageId(findable);
    }

    public DispatchMessage() {
        super();
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
