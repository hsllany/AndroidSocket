package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.utils.$Checkr;

/**
 * @author Yang Tao on 16/6/30.
 */
public abstract class Message implements Transimitable {

    public static final int SRC_SERVER = 0x01;
    public static final int SRC_CLIENT = 0x02;

    public static final int STATUS_SEND = 0x01;
    public static final int STATUS_TO_BE_SEND = 0x02;
    public static final int STATUS_FAILED = 0x03;
    public static final int STATUS_SERVER = 0x04;

    private final long timestamp = System.currentTimeMillis();

    /**
     * src indicate the source of the message, be one of {@code SRC_SERVER} or {@code SRC_CLIENT}
     */
    protected int src;

    private int status;

    public Message() {
        src = SRC_CLIENT;
        status = STATUS_TO_BE_SEND;
    }

    public final void updateStatus(int newStatus) {
        if (src == SRC_SERVER)
            throw new UnsupportedOperationException("can't modify the status");
        $Checkr.checkRange(newStatus, STATUS_FAILED, STATUS_SERVER, STATUS_SEND, STATUS_TO_BE_SEND);
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
}
