package com.vcg.mybatis.example.starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = JqlConfiguration.class)
public @interface EnableJqlAutoConfiguration {
}
