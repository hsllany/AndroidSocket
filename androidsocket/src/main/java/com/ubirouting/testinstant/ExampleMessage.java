package com.ubirouting.testinstant;

import com.ubirouting.instantmsglib.Findable;
import com.ubirouting.instantmsglib.msgs.InstantMessage;
import com.ubirouting.instantmsglib.processor.MessageAnnotation;
import com.ubirouting.instantmsglib.processor.MessageType;
import com.ubirouting.instantmsglib.serialization.bytelib.ToByte;

/**
 * @author Yang Tao on 16/7/4.
 */
@MessageAnnotation(
        code = 124,
        type = MessageType.ALL
)
public class ExampleMessage extends InstantMessage {

    @ToByte(order = 1)
    String message;

    public ExampleMessage(Findable findable, String message) {
        super(findable);

        this.message = message;
    }

    public ExampleMessage() {
        super();
    }

//    public byte[] bytes() {
//
//        byte[] messageBytes = instantMessage.getBytes();
//
//        byte[] finalBytes = new byte[messageBytes.length + 4 + 32];
//
//        PrimaryDatas.i2b(messageBytes.length, finalBytes, 0);
//        System.arraycopy(messageBytes, 0, finalBytes, 4, messageBytes.length);
//        System.arraycopy(getMessageId().bytes(), 0, finalBytes, 4 + messageBytes.length, 32);
//
//        return finalBytes;
//    }

}
