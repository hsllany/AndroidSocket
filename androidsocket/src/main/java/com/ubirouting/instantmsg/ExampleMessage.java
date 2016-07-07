package com.ubirouting.instantmsg;

import com.ubirouting.instantmsg.msgs.Message;
import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

/**
 * @author Yang Tao on 16/7/4.
 */
@MessageAnnotation(
        code = 2,
        type = MessageType.ALL
)
public class ExampleMessage implements Message {
    @Override
    public byte[] bytes() {
        return new byte[0];
    }
}
