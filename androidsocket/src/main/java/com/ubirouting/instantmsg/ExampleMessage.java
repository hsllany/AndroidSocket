package com.ubirouting.instantmsg;

import com.ubirouting.instantmsg.msgdispatcher.Findable;
import com.ubirouting.instantmsg.msgs.DispatchMessage;
import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

/**
 * @author Yang Tao on 16/7/4.
 */
@MessageAnnotation(
        code = 124,
        type = MessageType.ALL
)
public class ExampleMessage extends DispatchMessage {

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
//        byte[] messageBytes = message.getBytes();
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
