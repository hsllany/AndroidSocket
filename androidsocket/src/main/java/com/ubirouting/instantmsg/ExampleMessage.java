package com.ubirouting.instantmsg;

import com.ubirouting.instantmsg.msgs.DispatchMessage;
import com.ubirouting.instantmsg.processor.MessageAnnotation;
import com.ubirouting.instantmsg.processor.MessageType;

/**
 * @author Yang Tao on 16/7/4.
 */
@MessageAnnotation(
        code = 2,
        type = MessageType.ALL
)
public class ExampleMessage extends DispatchMessage {

    public ExampleMessage(byte[] rawBytes) {
        super(rawBytes);
    }

    @Override
    public byte[] bytes() {
        return new byte[0];
    }

    @Override
    protected void initWithBytes(byte[] bytes) {

    }
}
