package com.ubirouting.instantmsglib.msgs;

import com.ubirouting.instantmsglib.$Checkr;
import com.ubirouting.instantmsglib.Findable;
import com.ubirouting.instantmsglib.serialization.AbstractSerializer;
import com.ubirouting.instantmsglib.serialization.bytelib.ToByte;

/**
 * @author Yang Tao on 16/6/30.
 */
public abstract class InstantMessage implements Transmittable {
    public static final int SRC_SERVER = 0x01;
    public static final int SRC_CLIENT = 0x02;

    public static final int STATUS_SEND = 0x01;
    public static final int STATUS_TO_BE_SEND = 0x02;
    public static final int STATUS_FAILED = 0x03;
    public static final int STATUS_SERVER = 0x04;

    private final long timestamp = System.currentTimeMillis();
    /**
     * src indicate the source of the instantMessage, be one of {@code SRC_SERVER} or {@code SRC_CLIENT}
     */
    protected int src;
    private int status;

    private byte[] bytePool;

    @ToByte(order = ToByte.LAST)
    private MessageId mId;

    public InstantMessage(int source, int status, Findable findable) {
        src = source;
        this.status = status;
        mId = new MessageId(findable);
    }

    public InstantMessage() {
        src = SRC_CLIENT;
        status = STATUS_TO_BE_SEND;
        mId = new MessageId();
    }

    public InstantMessage(Findable findable) {
        src = SRC_CLIENT;
        this.status = STATUS_TO_BE_SEND;
        mId = new MessageId(findable);
    }

    public MessageId getMessageId() {
        return mId;
    }

    public final void updateStatus(int newStatus) {
        if (src == SRC_SERVER)
            throw new UnsupportedOperationException("can't modify the status");
        $Checkr.isInRange(newStatus, STATUS_FAILED, STATUS_SERVER, STATUS_SEND, STATUS_TO_BE_SEND);
        status = newStatus;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSource() {
        return src;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InstantMessage && ((InstantMessage) o).mId.equals(mId);

    }

    public void generateBytepool(AbstractSerializer serializer) {
        bytePool = serializer.buildBytes(this);
    }

    public void cacheBytepool(byte[] bytes) {
        this.bytePool = bytes;
    }

    boolean hasCacheBytepool() {
        return bytePool != null;
    }

    public void consumeBytepool() {
        bytePool = null;
    }
}
