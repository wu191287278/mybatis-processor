package com.vcg.simple.jdbc.processor;

import com.vcg.simple.jdbc.processor.domain.DialectEnums;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SimpleJdbc {


    /**
     * 使用spring上下文
     */
    boolean useSpring() default true;


    /**
     * 使用哪个数据库.
     */
    String dataSource() default "";


    /**
     * 从库,如果配置该项,自动轮询.
     */
    String[] slaveDataSources() default {};


    /**
     * 方言.
     *
     * @return 方言.
     */
    DialectEnums dialect() default DialectEnums.NONE;


    /**
     * default DomainName + SimpleExampleRepository
     */
    String repositoryName() default "";

}
