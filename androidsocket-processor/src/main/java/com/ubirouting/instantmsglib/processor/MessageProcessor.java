package com.ubirouting.instantmsglib.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Yang Tao on 16/7/1.
 */
@SupportedAnnotationTypes("MessageAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public final class MessageProcessor extends AbstractProcessor {

    private static final ClassName MESSAGE_TYPE = ClassName.get("com.ubirouting.instantmsglib.msgs", "InstantMessage");
    private static final ClassName CLASS_TYPE = ClassName.get(Class.class);
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private List<com.ubirouting.instantmsglib.processor.MessageClass> messageAnnotationList = new ArrayList<>();
    private Set<Integer> codeSet = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "-----start");
        System.out.println("start to build----");

        messageAnnotationList.clear();

        Class<MessageAnnotation> msgClass = MessageAnnotation.class;
        for (Element element : roundEnv.getElementsAnnotatedWith(msgClass)) {

            // check this element is a class and its package is com.ubirouting.instantmsg
            if (!isValid(element)) {
                return true;
            }


            messageAnnotationList.add(new com.ubirouting.instantmsglib.processor.MessageClass(element, element.getAnnotation(MessageAnnotation.class)));
            codeSet.add(element.getAnnotation(MessageAnnotation.class).code());

        }

        TypeSpec.Builder builder = TypeSpec.classBuilder("MessageFactory").addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        buildMessageFactoryJava(builder);
        buildMessageCodeJava(builder);

        JavaFile javaFile = JavaFile.builder("com.ubirouting.instantmsglib.msgs", builder.build()).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isValid(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            error(element, "only class can be annotated with @%s", MessageAnnotation.class.getSimpleName());
            return false;
        }

        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            error(element, "abstract class can not be annotated with @%s", MessageAnnotation.class.getSimpleName());
            return false;
        }

        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            error(element, "class should be public if you want to annotate it with @%s", MessageAnnotation.class.getSimpleName());
            return false;
        }


        TypeElement currentElement = (TypeElement) element;
        while (true) {
            TypeMirror superClassType = currentElement.getSuperclass();

            if (superClassType.getKind() == TypeKind.NONE) {
                error(element, "The class %s annotated with @%s must inherit from %s",
                        currentElement.getQualifiedName().toString(), MessageAnnotation.class.getSimpleName(),
                        MESSAGE_TYPE);
                return false;
            }

            if (superClassType.toString().equals(MESSAGE_TYPE.toString())) {
                break;
            }

            currentElement = (TypeElement) typeUtils.asElement(superClassType);
        }

        if (codeSet.contains(element.getAnnotation(MessageAnnotation.class).code())) {
            error(element, "code can't be the same");
            return false;
        }

        if (checkEmptyConstructor(element)) {
            return true;
        }

        error(element, "class should have at least public constructor with 1 parameters of byte array and an empty constructor");
        return false;

//        if (element.getAnnotation(MessageAnnotation.class).type() == MessageType.ALL || element.getAnnotation(MessageAnnotation.class).type() == MessageType.READ_ONLY) {
//            if (checkConstructorWithBytes(element)) {
//                return true;
//            } else {
//                  error(element, "class should have at least public constructor with 1 parameters of byte array and an empty constructor");
//                  return false;
//            }
//        } else {
//            return true;
//        }
    }

    private boolean checkConstructorWithBytes(Element element) {
        for (Element subElement : element.getEnclosedElements()) {
            if (subElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructElement = (ExecutableElement) subElement;

                if (constructElement.getModifiers().contains(Modifier.PUBLIC) && constructElement.getParameters().size() == 1) {
                    List variableElements = constructElement.getParameters();

                    VariableElement variableElement = (VariableElement) variableElements.get(0);
                    info(null, "lala" + variableElement.asType().toString().equals("byte[]"));
                    if (variableElement.asType().toString().equals("byte[]")) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    private boolean checkEmptyConstructor(Element element) {
        for (Element subElement : element.getEnclosedElements()) {
            if (subElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructElement = (ExecutableElement) subElement;

                if (constructElement.getModifiers().contains(Modifier.PUBLIC) && constructElement.getParameters().size() == 0) {
                    return true;
                }

            }
        }

        return false;
    }

    private void buildMessageFactoryJava(TypeSpec.Builder builder) {

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("messageTypeFromCode").addModifiers(Modifier.PUBLIC, Modifier.STATIC).
                addParameter(ParameterSpec.builder(TypeName.INT, "msgCode").build());

        methodSpecBuilder.addCode("switch(msgCode){\n");

        for (com.ubirouting.instantmsglib.processor.MessageClass messageClass : messageAnnotationList) {
            methodSpecBuilder.addStatement("case " + messageClass.code + ":\n return $T.class", messageClass.element);
        }

        methodSpecBuilder.addCode("default:\nreturn null;\n}\n");

        builder.addMethod(methodSpecBuilder.returns(CLASS_TYPE).build());

    }

    private void buildMessageCodeJava(TypeSpec.Builder builder) {

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("codeFromMessage").
                addModifiers(Modifier.STATIC, Modifier.PUBLIC).
                addParameter(ParameterSpec.builder(MESSAGE_TYPE, "msg").build());

        for (com.ubirouting.instantmsglib.processor.MessageClass messageClass : messageAnnotationList) {
            methodSpecBuilder.addStatement("if(msg.getClass().equals($T.class)) \n return " + messageClass.code, messageClass.element);
        }

        methodSpecBuilder.addStatement("return -1");

        builder.addMethod(methodSpecBuilder.returns(TypeName.INT).build());
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void info(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e);
    }
}
