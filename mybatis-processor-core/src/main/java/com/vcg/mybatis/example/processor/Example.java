package com.vcg.mybatis.example.processor;

import java.lang.annotation.*;

import com.vcg.mybatis.example.processor.annotation.ExampleQuery;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Example {

    /**
     * 命名空间.
     */
    String namespace();

    String leftEncode() default "`";

    String rightEncode() default "`";

    /**
     * Query对象属性.
     */
    ExampleQuery query() default @ExampleQuery;

}
