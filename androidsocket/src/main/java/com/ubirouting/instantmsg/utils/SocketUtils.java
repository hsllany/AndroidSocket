package com.ubirouting.instantmsg.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Yang Tao on 16/8/12.
 */
public class SocketUtils {

    private SocketUtils() {
    }

    public static void readBytes(InputStream in, byte[] buffer, int readLength) throws IOException {
        int start = 0;
        int left = readLength;
        while (start < readLength) {
            int r = in.read(buffer, start, left);
            if (r == -1) //reach the end of the stream
                throw new IOException("reach the end of the socket.");
            start += r;
            left -= r;
        }
    }

    public static void sendBytes(OutputStream out, byte[] buffer) throws IOException {
        out.write(buffer);
        out.flush();
    }

}
