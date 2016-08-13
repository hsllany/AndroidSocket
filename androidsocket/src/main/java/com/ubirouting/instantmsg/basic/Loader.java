package com.ubirouting.instantmsg.basic;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.ubirouting.instantmsg.msgs.MessageId;

/**
 * @author Yang Tao on 16/6/21.
 */
public class Loader {

    public static void Load(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        MessageId.MACHINE_CODE = telephonyManager.getDeviceId().hashCode();
    }
}
