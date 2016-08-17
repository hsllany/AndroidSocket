package com.ubirouting.instantmsg.serialization;

/**
 * @author Yang Tao on 16/8/17.
 */
public class Serializer {

    private static AbstractSerializer sSerializer = null;

    static {
        sSerializer = new DefaultSerializer();
    }

    private Serializer() {
    }

    /**
     * set the serializer for this whole SDK.
     *
     * @param serializer a AbstractSerializer, can't be null
     */
    public static void injectSerializer(AbstractSerializer serializer) {
        if (serializer == null) throw new NullPointerException("serializer can't be null");
        synchronized (Serializer.class) {
            sSerializer = serializer;
        }
    }

    public static AbstractSerializer serializer() {
        synchronized (Serializer.class) {
            return sSerializer;
        }
    }
}
