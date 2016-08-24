package com.ubirouting.instantmsg.utils;

/**
 * @author Yang Tao on 16/7/8.
 */
public class $Checkr {

    private $Checkr() {
    }

    public static boolean checkRange(int toBeChecked, String msg, int... range) {
        for (int rangeObj : range) {
            if (toBeChecked != rangeObj)
                return false;
        }

        return true;
    }

    public static boolean checkRange(long toBeChecked, long... range) {
        for (long rangeObj : range) {
            if (toBeChecked != rangeObj)
                return false;
        }

        return true;
    }

    public static boolean checkRange(float toBeChecked, float... range) {
        for (float rangeObj : range) {
            if (toBeChecked != rangeObj)
                return false;
        }

        return true;
    }


    public static boolean checkRange(double toBeChecked, double... range) {
        for (double rangeObj : range) {
            if (toBeChecked != rangeObj)
                return false;
        }

        return true;
    }

    public static boolean checkRange(Object toBeChecked, Object... range) {
        for (Object rangeObj : range) {
            if (!toBeChecked.equals(rangeObj))
                return false;
        }

        return true;
    }

    public static boolean inBetween(int toBeChecked, int smaller, int bigger) {
        return toBeChecked >= smaller && toBeChecked <= bigger;
    }

    public static boolean inBetween(long toBeChecked, long smaller, long bigger) {
        return toBeChecked >= smaller && toBeChecked <= bigger;
    }

    public static boolean inBetween(float toBeChecked, float smaller, float bigger) {
        return toBeChecked >= smaller && toBeChecked <= bigger;
    }

    public static boolean inBetween(double toBeChecked, double smaller, double bigger) {
        return toBeChecked >= smaller && toBeChecked <= bigger;
    }


    public static <T> T notNull(T object, String msg) {
        if (object == null)
            throw new NullPointerException(msg);

        return object;
    }
}
