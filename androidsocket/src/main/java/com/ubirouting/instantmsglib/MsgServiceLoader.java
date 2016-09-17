package com.ubirouting.instantmsglib;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.msgs.MessageId;

import org.jetbrains.annotations.NotNull;

/**
 * @author Yang Tao on 16/6/21.
 */
public class MsgServiceLoader {

    private static MsgServiceConfig sUniversalConfig;
    private static boolean hasInit = false;
    private static MsgProtocol sProtocol;

    public static void init(@NotNull Context context, @NotNull MsgServiceConfig config, @NotNull MsgProtocol protocol) {
        if (!hasInit) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MessageId.MACHINE_CODE = telephonyManager.getDeviceId().hashCode();

            sUniversalConfig = config;
            hasInit = true;
            sProtocol = protocol;

        } else
            throw new IllegalStateException("You have invoke the init function once, you can't call it again!");
    }

    public static MsgServiceConfig config() {
        return sUniversalConfig;
    }

    public static String protocolFromMessageDelegate(InstantMessage msg) {
        return sProtocol.protocolFromMessage(msg);
    }

    public static Class<?> messageTypeFromProtocolDelegate(String protocol) throws ClassNotFoundException {
        return sProtocol.messageTypeFromProtocol(protocol);
    }
}
