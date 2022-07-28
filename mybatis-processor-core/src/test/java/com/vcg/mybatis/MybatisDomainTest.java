package com.vcg.mybatis;


import java.io.File;
import java.net.MalformedURLException;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.vcg.mybatis.example.processor.MybatisDomainProcessor;
import org.junit.Test;

public class MybatisDomainTest {

    @Test
    public void emptyClassCompiles() throws MalformedURLException {
        final MybatisDomainProcessor processor = new MybatisDomainProcessor();
        File source = new File("TestProcessor.java");
        Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forResource(source.toURI().toURL()))
                .processedWith(processor)
                .compilesWithoutError();
    }

}
