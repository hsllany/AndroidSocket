package com.ubirouting.instantmsg.utils;

import java.util.Arrays;

/**
 * @author Yang Tao on 16/7/8.
 */
public class $Checkr {

    private $Checkr() {
    }

    public static boolean checkRange(int toBeChecked, int... range) {
        for (int rangeObj : range) {
            if (toBeChecked != rangeObj)
                throw new IllegalArgumentException(toBeChecked + " is not in range " + Arrays.toString(range));
        }

        return true;
    }
}
