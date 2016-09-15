package com.ubirouting.instantmsglib.serialization;

import com.ubirouting.instantmsglib.msgs.InstantMessage;

import org.jetbrains.annotations.NotNull;

/**
 * @author Yang Tao on 16/7/18.
 */
public interface AbstractSerializer {

    @NotNull
    Object buildViaBytes(@NotNull byte[] rawBytes);

    @NotNull
    byte[] buildWithObject(@NotNull InstantMessage message);
}
