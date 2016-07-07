package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.msgdispatcher.Findable;
import com.ubirouting.instantmsg.msgdispatcher.PrimaryDatas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yang Tao on 16/6/21.
 */
public final class MessageId {

    private static AtomicInteger sCounter = new AtomicInteger(0);

    public static long MACHINE_CODE;

    private final long timestamp;
    private final int pid;
    private final long machineId;
    private final long UIId;
    private final int counter;


    private final byte[] bytes = new byte[32];

    private MessageId(long timestamp, int pid, long machineId, long UIId, int counter) {
        this.timestamp = timestamp;
        this.pid = pid;
        this.machineId = machineId;
        this.UIId = UIId;
        this.counter = counter;

        generateByteArrays();
    }

    public MessageId(Findable findable) {
        this(System.currentTimeMillis() / 1000, android.os.Process.myPid(), MACHINE_CODE, findable.getFindableId(), sCounter.getAndIncrement());
    }

    public MessageId(byte[] bytesRaw) {
        this(PrimaryDatas.b2l(bytesRaw, 0), PrimaryDatas.b2i(bytesRaw, 8), PrimaryDatas.b2l(bytesRaw, 12), PrimaryDatas.b2l(bytesRaw, 20), PrimaryDatas.b2i(bytesRaw, 28));
    }


    public MessageId(String string) {
        this(string.getBytes());
    }

    public String getMessageIdString() {
        if (UIId == -1)
            throw new IllegalStateException("you have not set the UIId");
        return new String(bytes);
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

    public long getCounter() {
        return counter;
    }

    private synchronized void generateByteArrays() {
        PrimaryDatas.l2b(timestamp, bytes, 0);
        PrimaryDatas.i2b(pid, bytes, 8);
        PrimaryDatas.l2b(machineId, bytes, 12);
        PrimaryDatas.l2b(UIId, bytes, 20);
        PrimaryDatas.i2b(counter, bytes, 28);

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
