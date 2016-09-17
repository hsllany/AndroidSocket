package com.ubirouting.instantmsglib.serialization;

import com.ubirouting.instantmsglib.msgs.InstantMessage;

import org.jetbrains.annotations.NotNull;

/**
 * @author Yang Tao on 16/7/18.
 */
public interface AbstractSerializer {

    @NotNull
    Object buildObject(@NotNull byte[] rawBytes);

    @NotNull
    byte[] buildBytes(@NotNull InstantMessage message);
}
