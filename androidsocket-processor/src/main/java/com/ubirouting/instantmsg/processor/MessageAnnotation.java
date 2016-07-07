package com.ubirouting.instantmsg.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yang Tao on 16/7/1.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MessageAnnotation {
    int code() default 0;

    MessageType type() default MessageType.ALL;
}
