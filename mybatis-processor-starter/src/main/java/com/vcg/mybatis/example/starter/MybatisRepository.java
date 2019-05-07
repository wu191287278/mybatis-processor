package com.vcg.mybatis.example.starter;

import org.springframework.data.repository.Repository;

public interface MybatisRepository<T, ID> extends Repository<T, ID> {

}
