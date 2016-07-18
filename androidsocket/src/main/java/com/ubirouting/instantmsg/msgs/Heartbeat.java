package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

@MessageAnnotation(
        code = 1,
        type = MessageType.ALL
)
public class Heartbeat extends Message {

    public static Heartbeat HEARTBEAT_MSG = new Heartbeat();
    private static byte[] sHeartbeat = new byte[]{0};

    static {
        HEARTBEAT_MSG.src = Message.SRC_CLIENT;
    }

    public Heartbeat() {
        super();
    }


    @Override
    public String toString() {
        return "[com.ubirouting.instantmsg.msgs.Heartbeat]";
    }
}
