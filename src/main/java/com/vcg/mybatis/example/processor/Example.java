package com.vcg.mybatis.example.processor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Example {

    /**
     * mybatis 接口类名
     *
     * @return
     */
    String namespace();

}
