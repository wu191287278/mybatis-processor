package com.vcg.mybatis.example.processor;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MybatisExampleRepository<T, ID, Example> {

    T selectByPrimaryKey(ID id);

    List<T> selectByPrimaryKeys(List<ID> ids);

    List<T> selectByExample(Example query);

    List<Map<String, Object>> selectByExampleWithMap(Example query);

    void insert(T t);

    void insertSelective(T t);

    void insertBatch(List<T> ts);

    void insertByExample(Example query);

    void insertSelectiveByExample(Example query);

    void insertBatchByExample(Example query);

    long countByExample(Example query);

    int updateByPrimaryKeySelective(T t);

    int updateByPrimaryKey(T t);

    int updateByExampleSelective(@Param("record") T t, @Param("example") Example query);

    int updateByExample(@Param("record") T t, @Param("example") Example query);

    int deleteByPrimaryKey(ID id);

    int deleteByPrimaryKeys(List<ID> ids);

    int deleteByExample(Example query);

    boolean existById(ID id);

    Boolean existByExample(Example query);

}
