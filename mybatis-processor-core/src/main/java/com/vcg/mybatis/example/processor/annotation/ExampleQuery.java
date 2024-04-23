package com.vcg.mybatis.example.processor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ExampleQuery {

    /**
     * 是否开启.
     *
     * @return true/false.
     */
    boolean enable() default false;


    /**
     * 日期格式化.
     *
     * @return 日期格式化.
     */
    String dateFormat() default "";

    /**
     * 是否数字格式化.
     *
     * @return 是否数字格式化.
     */
    boolean numberFormat() default true;

    /**
     * 全部字段.
     *
     * @return 是否全部字段.
     */
    boolean all() default false;

    /**
     * 页码字段名.
     *
     * @return 页码.
     */
    String page() default "page";

    /**
     * 每页数量字段名.
     *
     * @return 每页大小.
     */
    String size() default "size";

    /**
     * 生成排序字段名.
     *
     * @return 排序字段.
     */
    String orderBy() default "orderBy";

    /**
     * 生成排序字段名.
     *
     * @return ASC/DESC.
     */
    String sort() default "sort";


    /**
     * 默认页码.
     *
     * @return 默认页码.
     */
    int pageDefault() default 1;

    /**
     * 默认每页数量.
     *
     * @return 默认每页数量.
     */
    int sizeDefault() default 20;

    /**
     * 默认排序字段.
     *
     * @return 排序默认字段.
     */
    String orderByDefault() default "";


    /**
     * 默认排序.
     *
     * @return 排序默认值.
     */
    String sortDefault() default "";

}
