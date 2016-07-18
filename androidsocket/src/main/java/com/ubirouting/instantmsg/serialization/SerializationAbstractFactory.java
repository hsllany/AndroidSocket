package com.ubirouting.instantmsg.serialization;

import com.ubirouting.instantmsg.msgs.Transimitable;

/**
 * @author Yang Tao on 16/7/18.
 */
public interface SerializationAbstractFactory {

    <T extends Transimitable> T buildViaBytes(byte[] rawBytes, Class<T> tClass);

    byte[] buildWithObject(Transimitable transimitable);
}
