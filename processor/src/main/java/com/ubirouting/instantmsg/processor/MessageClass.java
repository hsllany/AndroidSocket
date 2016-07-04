package com.ubirouting.instantmsg.processor;

import javax.lang.model.element.Element;

/**
 * @author Yang Tao on 16/7/4.
 */
public class MessageClass {
    String className;
    int code;
    MessageType type;

    MessageClass(Element element, MessageAnnotation annotation) {
        className = element.getSimpleName().toString();

        code = annotation.code();
        type = annotation.type();
    }
}
