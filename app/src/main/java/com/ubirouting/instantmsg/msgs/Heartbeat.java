package com.ubirouting.instantmsg.msgs;

import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

@MessageAnnotation(code = 1, type = MessageType.ALL)
public class Heartbeat implements Message {

    private static byte[] sHeartbeat = new byte[]{1, 0, 0, 0, 0};

    @Override
    public byte[] bytes() {
        return sHeartbeat;
    }
}
