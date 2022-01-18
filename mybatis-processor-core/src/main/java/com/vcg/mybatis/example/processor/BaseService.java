package com.vcg.mybatis.example.processor;


import java.util.*;
import java.util.function.Function;

import com.vcg.mybatis.example.processor.domain.PageInfo;


public class BaseService<T, ID, Example> {

    private final MybatisExampleRepository<T, ID, Example> repository;

    private final Function<T, ID> apply;

    public BaseService(MybatisExampleRepository<T, ID, Example> repository) {
        this.repository = repository;
        this.apply = null;
    }

    public BaseService(MybatisExampleRepository<T, ID, Example> repository, Function<T, ID> apply) {
        this.repository = repository;
        this.apply = apply;
    }

    public T selectByPrimaryKey(ID id) {
        return repository.selectByPrimaryKey(id);
    }

    public List<T> selectByPrimaryKeys(List<ID> ids) {
        if (removeIfNull(ids).isEmpty()) {
            return new ArrayList<>();
        }
        return repository.selectByPrimaryKeys(ids);
    }

    public List<T> selectByPrimaryKeysWithSorted(List<ID> ids) {
        if (removeIfNull(ids).isEmpty()) {
            return new ArrayList<>();
        }
        return repository.selectByPrimaryKeysWithSorted(ids, apply);
    }

    public List<T> selectByExample(Example example) {
        return this.repository.selectByExample(example);
    }

    public T selectOne(Example example) {
        return this.repository.selectOne(example);
    }

    public void insert(T t) {
        this.repository.insert(t);
    }

    public void insertSelective(T t) {
        this.repository.insertSelective(t);
    }

    public void insertBatch(List<T> ts) {
        this.repository.insertBatch(ts);
    }

    public void upsert(T t) {
        this.repository.upsert(t);
    }

    public void upsertSelective(T t) {
        this.repository.upsertSelective(t);
    }

    public int updateByPrimaryKeySelective(T t) {
        return this.repository.updateByPrimaryKeySelective(t);
    }

    public int updateByExampleSelective(T t, Example q) {
        return this.repository.updateByExampleSelective(t, q);
    }

    public int updateByExample(T t, Example q) {
        return this.repository.updateByExample(t, q);
    }

    public int updateByPrimaryKey(T t) {
        return this.repository.updateByPrimaryKey(t);
    }

    public long countByExample(Example example) {
        return this.repository.countByExample(example);
    }

    public int deleteByPrimaryKey(ID id) {
        return this.repository.deleteByPrimaryKey(id);
    }

    public int deleteByPrimaryKeys(List<ID> ids) {
        if (removeIfNull(ids).isEmpty()) {
            return 0;
        }
        return this.repository.deleteByPrimaryKeys(ids);
    }

    public int deleteByExample(Example q) {
        return this.repository.deleteByExample(q);
    }

    public void replaceByPrimaryKey(T t) {
        int affect = updateByPrimaryKey(t);
        if (affect == 0) {
            insert(t);
        }
    }

    public void replaceByPrimaryKeySelective(T t) {
        int affect = updateByPrimaryKeySelective(t);
        if (affect == 0) {
            insertSelective(t);
        }
    }

    public Map<ID, T> mapById(List<ID> ids) {
        if (removeIfNull(ids).isEmpty()) {
            return new HashMap<>();
        }
        return repository.mapById(ids, apply);
    }

    public PageInfo<T> pageByExample(int page, int size, Example q) {
        List<T> ts = selectByExample(q);
        long total = ts.size();
        if (page == 1 && total < size) {
            total = ts.size();
        } else {
            total = countByExample(q);
        }
        return new PageInfo<>(page, size, total, ts);
    }

    private List<ID> removeIfNull(List<ID> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        ids.removeIf(Objects::isNull);
        return ids;
    }
}
