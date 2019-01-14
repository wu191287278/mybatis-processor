package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.MybatisExampleRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MybatisCrudRepository<T, ID, Example>
        extends MybatisExampleRepository<T, ID, Example>, Repository<T, ID> {

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
