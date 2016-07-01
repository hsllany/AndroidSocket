package com.ubirouting.instantmsg.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * @author Yang Tao on 16/7/1.
 */
@SupportedAnnotationTypes("com.ubirouting.instantmsg.processor.MessageAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MessageProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("hello world");
        return false;
    }
}
