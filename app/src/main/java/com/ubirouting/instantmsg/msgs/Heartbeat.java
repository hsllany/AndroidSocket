package com.ubirouting.instantmsg.msgs;

public class Heartbeat implements Message {

    private static byte[] sHeartbeat = new byte[]{1, 0, 0, 0, 0};

    @Override
    public byte[] bytes() {
        return sHeartbeat;
    }
}
