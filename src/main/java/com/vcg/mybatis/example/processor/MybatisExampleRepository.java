package com.vcg.mybatis.example.processor;

import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
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

    default <S extends T> S save(S entity) {
        insertSelective(entity);
        return entity;
    }

    default <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        if (entities instanceof List) {
            insertBatch((List<T>) entities);
        } else {
            List<T> list = new ArrayList<>();
            for (S entity : entities) {
                list.add(entity);
            }
            insertBatch(list);
        }
        return entities;
    }

    T getOne(ID id);

    T getById(ID id);

    default Optional<T> findById(ID id) {
        T t = selectByPrimaryKey(id);
        return t != null ? Optional.of(t) : Optional.empty();
    }

    boolean existsById(ID id);

    Iterable<T> findAll();

    Iterable<T> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(T entity);

    void deleteAll(Iterable<? extends T> entities);

}
