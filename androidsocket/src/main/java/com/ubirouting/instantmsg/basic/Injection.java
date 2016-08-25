package com.ubirouting.instantmsg.basic;

import com.ubirouting.instantmsg.serialization.DefaultSerializer;

/**
 * @author Yang Tao on 16/8/24.
 */
public class Injection {

    private Injection() {
    }

    public static DefaultSerializer provideSerializer() {
        return DefaultSerializer.getInstance();
    }

}
