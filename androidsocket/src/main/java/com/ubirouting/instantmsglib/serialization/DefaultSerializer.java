package com.ubirouting.instantmsglib.serialization;

import com.ubirouting.instantmsglib.MsgServiceLoader;
import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.serialization.bytelib.ByteUtils;
import com.ubirouting.instantmsglib.serialization.bytelib.PrimaryDatas;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Yang Tao on 16/7/18.
 */
public class DefaultSerializer implements AbstractSerializer {

    @Override
    @NotNull
    public Object buildObject(@NotNull byte[] rawBytes) {
        int codeStringLength = PrimaryDatas.b2i(rawBytes, 0);
        String protocol = new String(rawBytes, 4, codeStringLength, Charset.forName("UTF-8"));
        byte[] msgByte = Arrays.copyOfRange(rawBytes, 4 + codeStringLength, rawBytes.length);
        Class<?> clazz = null;
        try {
            clazz = MsgServiceLoader.messageTypeFromProtocolDelegate(protocol);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Error("wrong code");
        }
        return ByteUtils.toObject(msgByte, clazz);
    }

    @Override
    @NotNull
    public byte[] buildBytes(@NotNull InstantMessage message) {
        String protocol = MsgServiceLoader.protocolFromMessageDelegate(message);
        byte[] msgBytes = ByteUtils.toByte(message);
        byte[] protocolBytes = protocol.getBytes(Charset.forName("UTF-8"));

        byte[] encodeBytes = new byte[msgBytes.length + 4 + protocolBytes.length];

        PrimaryDatas.i2b(protocolBytes.length, encodeBytes, 0);
        System.arraycopy(protocolBytes, 0, encodeBytes, 4, protocolBytes.length);
        System.arraycopy(msgBytes, 0, encodeBytes, 4 + protocolBytes.length, msgBytes.length);
        return encodeBytes;
    }
}
