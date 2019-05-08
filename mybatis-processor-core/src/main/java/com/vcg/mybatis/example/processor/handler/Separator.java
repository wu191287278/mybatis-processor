package com.vcg.mybatis.example.processor.handler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Separator {

    String typeHandler() default "com.vcg.mybatis.example.processor.handler.PlaceHolderTypeHandler";

    String value() default ",";

}
