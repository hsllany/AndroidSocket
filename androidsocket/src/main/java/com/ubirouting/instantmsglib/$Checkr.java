package com.ubirouting.instantmsglib;

import java.util.Arrays;

/**
 * @author Yang Tao on 16/7/8.
 */
public class $Checkr {

    private $Checkr() {
    }


    public static boolean isInRange(long toBeChecked, long... range) {
        for (long rangeObj : range) {
            if (toBeChecked != rangeObj)
                return false;
        }

        return true;
    }


    public static boolean isInRange(double toBeChecked, double... range) {
        for (double rangeObj : range) {
            if (toBeChecked != rangeObj)
                return false;
        }

        return true;
    }

    public static boolean isInRangeObj(Object toBeChecked, Object... range) {
        for (Object rangeObj : range) {
            if (!toBeChecked.equals(rangeObj))
                return false;
        }

        return true;
    }

    public static void checkInRange(long toBeChecked, long... range) {
        if (!isInRange(toBeChecked, range)) {
            throw new IllegalArgumentException("val must be one of " + Arrays.toString(range));
        }
    }


    public static void checkInRange(double toBeChecked, double... range) {
        if (!isInRange(toBeChecked, range)) {
            throw new IllegalArgumentException("val must be one of " + Arrays.toString(range));
        }
    }


    public static boolean inBetween(long toBeChecked, long smaller, long bigger) {
        return toBeChecked >= smaller && toBeChecked <= bigger;
    }


    public static boolean inBetween(double toBeChecked, double smaller, double bigger) {
        return toBeChecked >= smaller && toBeChecked <= bigger;
    }

    public static void checkInBetween(long toBeChecked, long smaller, long bigger) {
        if (!inBetween(toBeChecked, smaller, bigger)) {
            throw new IllegalArgumentException("val must be between " + smaller + " and " + bigger);
        }
    }

    public static void checkInBetween(double toBeChecked, double smaller, double bigger) {
        if (!inBetween(toBeChecked, smaller, bigger)) {
            throw new IllegalArgumentException("val must be between " + smaller + " and " + bigger);
        }
    }


    public static <T> T notNull(T object, String msg) {
        if (object == null)
            throw new NullPointerException(msg);

        return object;
    }

    public static void checkPositive(long val) {
        if (val < 0)
            throw new IllegalArgumentException("parameter must be greater or equal to 0!");
    }

    public static void checkPositive(double val) {
        if (val < 0)
            throw new IllegalArgumentException("parameter must be greater or equal to 0!");
    }
}
