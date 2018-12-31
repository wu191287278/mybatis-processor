package com.vcg.mybatis.example.processor;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface MybatisExampleRepository<T, ID, Example> {

    T selectByPrimaryKey(ID id);

    List<T> selectByPrimaryKeys(List<ID> ids);

    List<T> selectByExample(Example query);

    void insert(T t);

    void insertSelective(T t);

    void insertBatch(List<T> ts);

    long countByExample(Example query);

    int updateByPrimaryKeySelective(T t);

    int updateByPrimaryKey(T t);

    int updateByExampleSelective(@Param("record") T t, @Param("example") Example query);

    int updateByExample(@Param("record") T t, @Param("example") Example query);

    int deleteByPrimaryKey(ID id);

    int deleteByPrimaryKeys(List<ID> ids);

    int deleteByExample(Example query);

    T getOne(ID id);

    T getById(ID id);

    boolean existsById(ID id);

    long count();

    void deleteById(ID id);

    void delete(T entity);

    void deleteAll(List<? extends T> entities);

    List<T> findAll();

    List<T> findAllById(List<ID> ids);

    default Optional<T> findById(ID id) {
        T t = selectByPrimaryKey(id);
        return t != null ? Optional.of(t) : Optional.empty();
    }

    default T save(T entity) {
        insertSelective(entity);
        return entity;
    }

    default List<T> saveAll(List<T> entities) {
        insertBatch(entities);
        return entities;
    }
}
