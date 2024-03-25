package com.vcg.mybatis.example.processor;

import java.lang.annotation.*;

import com.vcg.mybatis.example.processor.annotation.ExampleQuery;
import com.vcg.mybatis.example.processor.domain.DialectEnums;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Example {

    /**
     * 命名空间.
     * @return 命名空间.
     */
    String namespace();

    /**
     * 方言.
     * @return 方言.
     */
    DialectEnums dialect() default DialectEnums.NONE;

    /**
     * Query对象属性.
     * @return Query对象属性.
     */
    ExampleQuery query() default @ExampleQuery;

}
