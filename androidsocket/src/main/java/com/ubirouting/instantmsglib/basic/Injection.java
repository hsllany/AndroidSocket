package com.ubirouting.instantmsglib.basic;

import com.ubirouting.instantmsglib.serialization.DefaultSerializer;

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
