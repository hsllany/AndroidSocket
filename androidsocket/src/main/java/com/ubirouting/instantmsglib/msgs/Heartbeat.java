package com.ubirouting.instantmsglib.msgs;

import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

@MessageAnnotation(
        code = 1,
        type = MessageType.ALL
)
public class Heartbeat extends InstantMessage {

    public static Heartbeat HEARTBEAT_MSG = new Heartbeat();
    private static byte[] sHeartbeat = new byte[]{0};

    static {
        HEARTBEAT_MSG.src = InstantMessage.SRC_CLIENT;
    }

    public Heartbeat() {
        super();
    }


    @Override
    public String toString() {
        return "[Heartbeat]";
    }
}
