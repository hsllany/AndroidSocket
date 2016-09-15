package com.ubirouting.instantmsglib.msgs;

import com.ubirouting.instantmsglib.basic.Findable;
import com.ubirouting.instantmsglib.serialization.bytelib.PrimaryDatas;
import com.ubirouting.instantmsglib.serialization.bytelib.ToByte;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yang Tao on 16/6/21.
 */
public final class MessageId implements Transimitable {

    public static final int NO_FINDABLE = Integer.MIN_VALUE;
    public static long MACHINE_CODE;
    private static AtomicInteger sCounter = new AtomicInteger(0);
    @ToByte(order = 1)
    private final long timestamp;

    @ToByte(order = 2)
    private final int pid;

    @ToByte(order = 3)
    private final long machineId;
    @ToByte(order = 5)
    private final int counter;
    @ToByte(order = 4)
    private long UIId;

    private MessageId(long timestamp, int pid, long machineId, long UIId, int counter) {
        this.timestamp = timestamp;
        this.pid = pid;
        this.machineId = machineId;
        this.UIId = UIId;
        this.counter = counter;

    }

    public MessageId(int findableId) {
        this(System.currentTimeMillis() / 1000, android.os.Process.myPid(), MACHINE_CODE, findableId, sCounter.getAndIncrement());
    }

    public MessageId(Findable findable) {
        this(findable.getFindableId());
    }

    public MessageId() {
        this(NO_FINDABLE);
    }

    public String getMessageIdString() {
        if (UIId == -1)
            throw new IllegalStateException("you have not set the UIId");
        return new String(generateByteArrays());
    }

    public int getPid() {
        return pid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getMachineId() {
        return machineId;
    }

    public long getUIId() {
        return UIId;
    }

    public void setUIId(long UIId) {
        this.UIId = UIId;
    }

    public long getCounter() {
        return counter;
    }

    private synchronized byte[] generateByteArrays() {
        byte[] bytes = new byte[32];
        PrimaryDatas.l2b(timestamp, bytes, 0);
        PrimaryDatas.i2b(pid, bytes, 8);
        PrimaryDatas.l2b(machineId, bytes, 12);
        PrimaryDatas.l2b(UIId, bytes, 20);
        PrimaryDatas.i2b(counter, bytes, 28);
        return bytes;
    }

    @Override
    public String toString() {
        return timestamp + "-" + pid + "-" + machineId + "-" + UIId + "-" + counter;
    }

    @Override
    public int hashCode() {
        int r = 37;
        r = r * 37 + (int) (timestamp ^ (timestamp >>> 32));
        r = r * 37 + (int) (machineId ^ (machineId >>> 32));
        r = r * 37 + (int) (UIId ^ (UIId >>> 32));
        r = r * 37 + pid;
        r = r * 37 + counter;

        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MessageId) {
            MessageId oo = (MessageId) o;
            return oo.timestamp == this.timestamp && oo.machineId == this.machineId && oo.UIId == this.UIId && oo.pid == this.pid && oo.counter == this.counter;
        }

        return false;
    }
}
