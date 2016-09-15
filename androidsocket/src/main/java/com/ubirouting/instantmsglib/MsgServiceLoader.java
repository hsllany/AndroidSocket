package com.ubirouting.instantmsglib;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.ubirouting.instantmsglib.msgs.MessageId;

import org.jetbrains.annotations.NotNull;

/**
 * @author Yang Tao on 16/6/21.
 */
public class MsgServiceLoader {

    private static MsgServiceConfig sUniversalConfig;

    public static void init(@NotNull Context context, @NotNull MsgServiceConfig config) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        MessageId.MACHINE_CODE = telephonyManager.getDeviceId().hashCode();

        sUniversalConfig = config;
    }

    public static MsgServiceConfig config() {
        return sUniversalConfig;
    }
}
