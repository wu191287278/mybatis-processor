package com.vcg.mybatis.example.processor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ExampleQuery {

    boolean enable() default false;


    String dateFormat() default "";

    boolean numberFormat() default true;

    /**
     * 全部字段.
     */
    boolean all() default false;

    /**
     * 页码字段名.
     */
    String page() default "page";

    /**
     * 每页数量字段名.
     */
    String size() default "size";

    /**
     * 生成排序字段名.
     */
    String orderBy() default "orderBy";

    /**
     * 生成排序字段名.
     */
    String sort() default "sort";


    /**
     * 默认页码.
     */
    int pageDefault() default 1;

    /**
     * 默认每页数量.
     */
    int sizeDefault() default 20;

    /**
     * 默认排序字段.
     */
    String orderByDefault() default "";


    /**
     * 默认排序.
     */
    String sortDefault() default "";

}
