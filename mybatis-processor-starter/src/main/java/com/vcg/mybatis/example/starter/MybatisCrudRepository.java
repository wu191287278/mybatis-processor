package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.MybatisExampleRepository;

public interface MybatisCrudRepository<T, ID, Example> extends MybatisRepository<T, ID>, MybatisExampleRepository<T, ID, Example> {

}
