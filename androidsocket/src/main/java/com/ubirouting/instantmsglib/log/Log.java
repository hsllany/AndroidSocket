package com.ubirouting.instantmsglib.log;

/**
 * @author Yang Tao on 16/9/12.
 */
public interface Log {

    void logD(String msg);

    void logE(String error);

    void logE(Exception e);
}
