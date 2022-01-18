package com.vcg.mybatis.example.starter;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IncludeColumns {

    String[] value();
}
