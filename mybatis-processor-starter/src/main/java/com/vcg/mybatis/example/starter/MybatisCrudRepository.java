package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.MybatisExampleRepository;

import java.util.stream.Stream;

public interface MybatisCrudRepository<T, ID, Example> extends MybatisRepository<T, ID>, MybatisExampleRepository<T, ID, Example> {

    Stream<T> streamAll();

    Stream<T> streamByExample(Example query);

}
