package com.ubirouting.instantmsg.serialization;

import com.ubirouting.instantmsg.msgs.Transimitable;

/**
 * @author Yang Tao on 16/7/18.
 */
public class DefaultSerializationFactory implements SerializationAbstractFactory {
    @Override
    public <T extends Transimitable> T buildViaBytes(byte[] rawBytes, Class<T> tClass) {
        return null;
    }

    @Override
    public byte[] buildWithObject(Transimitable transimitable) {
        return new byte[0];
    }
}
