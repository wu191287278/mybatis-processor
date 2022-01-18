package com.vcg.mybatis.example.processor.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Criterion {

    /**
     * 字段名称.
     */
    String value() default "";

    boolean equalTo() default false;

    boolean notEqualTo() default false;

    boolean in() default false;

    boolean notIn() default false;

    boolean lessThan() default false;

    boolean lessThanOrEqualTo() default false;

    boolean greaterThan() default false;

    boolean greaterThanOrEqualTo() default false;

    boolean like() default false;

    boolean notLike() default false;

    boolean between() default false;

    String[] or() default {};

}
