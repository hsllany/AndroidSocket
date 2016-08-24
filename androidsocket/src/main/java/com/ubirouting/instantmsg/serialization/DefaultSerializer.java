package com.ubirouting.instantmsg.serialization;

import com.ubirouting.instantmsg.msgs.Transimitable;
import com.ubirouting.instantmsg.serialization.bytelib.ByteUtils;

/**
 * @author Yang Tao on 16/7/18.
 */
public class DefaultSerializer implements AbstractSerializer {

    private static DefaultSerializer instance;

    public static DefaultSerializer getInstance() {
        if (instance == null) {
            synchronized (DefaultSerializer.class) {
                if (instance == null)
                    instance = new DefaultSerializer();
            }
        }

        return instance;
    }

    @Override
    public <T extends Transimitable> T buildViaBytes(byte[] rawBytes, Class<T> tClass) {
        return ByteUtils.toObject(rawBytes, tClass);
    }

    @Override
    public byte[] buildWithObject(Transimitable transimitable) {
        return ByteUtils.toByte(transimitable);
    }
}
