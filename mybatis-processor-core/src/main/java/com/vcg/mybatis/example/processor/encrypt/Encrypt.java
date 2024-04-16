package com.vcg.mybatis.example.processor.encrypt;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Encrypt {

    /**
     * 加解密使用类.
     */
    String value() default "com.vcg.mybatis.example.processor.encrypt.EncryptStringTypeHandler";

}
