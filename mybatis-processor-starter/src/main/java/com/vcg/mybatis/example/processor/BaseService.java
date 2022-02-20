package com.vcg.mybatis.example.processor;


import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public void insert(T t) {
        this.repository.insert(t);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertSelective(T t) {
        this.repository.insertSelective(t);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(List<T> ts) {
        this.repository.insertBatch(ts);
    }

    @Transactional(rollbackFor = Exception.class)
    public void upsert(T t) {
        this.repository.upsert(t);
    }

    @Transactional(rollbackFor = Exception.class)
    public void upsertSelective(T t) {
        this.repository.upsertSelective(t);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateByPrimaryKeySelective(T t) {
        return this.repository.updateByPrimaryKeySelective(t);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateByExampleSelective(T t, Example q) {
        return this.repository.updateByExampleSelective(t, q);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateByExample(T t, Example q) {
        return this.repository.updateByExample(t, q);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateByPrimaryKey(T t) {
        return this.repository.updateByPrimaryKey(t);
    }

    public long countByExample(Example example) {
        return this.repository.countByExample(example);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(ID id) {
        return this.repository.deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeys(List<ID> ids) {
        if (removeIfNull(ids).isEmpty()) {
            return 0;
        }
        return this.repository.deleteByPrimaryKeys(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByExample(Example q) {
        return this.repository.deleteByExample(q);
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceByPrimaryKey(T t) {
        int affect = repository.updateByPrimaryKey(t);
        if (affect == 0) {
            repository.insert(t);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceByPrimaryKeySelective(T t) {
        int affect = repository.updateByPrimaryKeySelective(t);
        if (affect == 0) {
            repository.insertSelective(t);
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
