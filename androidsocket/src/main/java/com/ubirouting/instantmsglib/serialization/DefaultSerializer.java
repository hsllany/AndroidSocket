package com.ubirouting.instantmsglib.serialization;

import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.msgs.MessageFactory;
import com.ubirouting.instantmsglib.serialization.bytelib.ByteUtils;
import com.ubirouting.instantmsglib.serialization.bytelib.PrimaryDatas;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
    public Object buildViaBytes(@NotNull byte[] rawBytes) {
        int code = PrimaryDatas.b2i(rawBytes, 0);
        byte[] msgByte = Arrays.copyOfRange(rawBytes, 4, rawBytes.length);
        Class<?> clazz = MessageFactory.messageTypeFromCode(code);
        return ByteUtils.toObject(msgByte, clazz);
    }

    @Override
    public byte[] buildWithObject(@NotNull InstantMessage message) {
        int code = MessageFactory.codeFromMessage(message);
        byte[] msgBytes = ByteUtils.toByte(message);

        byte[] encodeBytes = new byte[msgBytes.length + 4];
        PrimaryDatas.i2b(code, encodeBytes, 0);
        System.arraycopy(msgBytes, 0, encodeBytes, 4, msgBytes.length);
        return encodeBytes;
    }
}
