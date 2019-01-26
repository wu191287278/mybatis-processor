package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.MybatisExampleRepository;
import org.springframework.data.repository.Repository;

public interface MybatisCrudRepository<T, ID, Example> extends Repository<T, ID>, MybatisExampleRepository<T, ID, Example> {
}
