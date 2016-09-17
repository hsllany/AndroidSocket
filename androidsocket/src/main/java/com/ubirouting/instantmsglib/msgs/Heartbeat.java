package com.ubirouting.instantmsglib.msgs;

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
