package com.ubirouting.instantmsg.msgservice;

/**
 * @author Yang Tao on 16/7/7.
 */
public final class MsgServiceConfig {

    /**
     * the socket server address
     */
    public static String HOST = "192.168.1.105";
    /**
     * the socket server port
     */
    public static int PORT = 10002;
    public static int MAX_HEARTBEAT_TIME = 40000;
    public static int MIN_HEARTBEAT_TIME = 5000;
    public static boolean DEBUG_SOCKET = true;
    static int SOCKET_TIME_OUT = (int) (MAX_HEARTBEAT_TIME * 1.5f);
    static int MESSAGE_SEND_TIME_OUT = MIN_HEARTBEAT_TIME;
    static int CONNECT_TRY_TIMEOUT = 1000;

    private MsgServiceConfig() {
    }
}
