package com.ubirouting.instantmsglib;

/**
 * @author Yang Tao on 16/9/12.
 */
public class MsgServiceConfig {

    private int maxHeartbeatInterval = 40000;


    private int minHeartbeatTime = 5000;

    /**
     * control whether to print log
     */
    private boolean printMsgLog = false;

    /**
     * the socket server address
     */
    private String host;

    /**
     * the socket server port
     */
    private int port = 10002;

    /**
     * control the sleep interval for each connecting action
     */
    private int connectRetryInterval = 1000;

    private MsgServiceConfig() {

    }

    public int socketTimeOut() {
        return (int) (maxHeartbeatInterval * 1.5f);
    }

    public int megSendTimeOut() {
        return minHeartbeatTime;
    }

    public int connectRetryInterval() {
        return connectRetryInterval;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public int minHeartbeatTime() {
        return minHeartbeatTime;
    }

    public boolean isPrintMsgLog() {
        return printMsgLog;
    }

    public int maxHeartbeatTime() {
        return maxHeartbeatInterval;
    }

    public static class Builder {
        private MsgServiceConfig obj;

        public Builder() {
            obj = new MsgServiceConfig();
        }

        public Builder withHostAndPort(String host, int port) {
            obj.host = host;
            $Checkr.checkPositive(port);
            obj.port = port;
            return this;
        }

        public Builder withHeartbeatTime(int minHeartbeatInterval, int maxHeartbeatInterval) {
            $Checkr.checkPositive(minHeartbeatInterval);
            $Checkr.checkPositive(maxHeartbeatInterval);

            if (minHeartbeatInterval > maxHeartbeatInterval)
                throw new IllegalArgumentException("minHeartbeatInterval can't be greater than maxHeartbeatInterval");
            obj.maxHeartbeatInterval = maxHeartbeatInterval;
            obj.minHeartbeatTime = minHeartbeatInterval;
            return this;
        }

        public Builder withPrintLog() {
            obj.printMsgLog = true;
            return this;
        }

        public Builder withConnectRetryInterval(int connectTimeout) {
            $Checkr.checkPositive(connectTimeout);
            obj.connectRetryInterval = connectTimeout;
            return this;
        }

        public MsgServiceConfig build() {
            return obj;
        }
    }

}
