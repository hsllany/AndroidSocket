package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

@MessageAnnotation(
        code = 1,
        type = MessageType.ALL
)
public class Heartbeat extends Message {

    public static Heartbeat HEARTBEAT_MSG = new Heartbeat();
    private static byte[] sHeartbeat = new byte[]{1, 0, 0, 0, 1};

    static {
        HEARTBEAT_MSG.src = Message.SRC_CLIENT;
    }

    public Heartbeat() {

    }

    public Heartbeat(byte[] rawBytes) {
        super(rawBytes);
    }

    @Override
    public byte[] bytes() {
        return sHeartbeat;
    }

    @Override
    protected void initWithBytes(byte[] bytes) {

    }
}
