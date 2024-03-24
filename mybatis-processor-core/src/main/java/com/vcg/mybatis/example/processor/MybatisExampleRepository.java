package com.vcg.mybatis.example.processor;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface MybatisExampleRepository<T, ID, Example> {

    T selectByPrimaryKey(ID id);

    T selectByPrimaryKeyForUpdate(ID id);

    List<T> selectByPrimaryKeys(List<ID> ids);

    List<T> selectByExample(Example query);

    default T selectOne(Example query) {
        List<T> ts = selectByExample(query);
        return ts.isEmpty() ? null : ts.get(0);
    }

    List<T> selectAll();

    Cursor<T> cursorAll();

    Cursor<T> cursorByExample(Example query);

    List<Map<String, Object>> selectByExampleWithMap(Example query);

    void insert(T t);

    void insertSelective(T t);

    void upsert(T t);

    void upsertSelective(T t);

    void insertBatch(List<T> ts);

    long countByExample(Example query);

    long count();

    int updateByPrimaryKeySelective(T t);

    int updateByPrimaryKey(T t);

    int updateByExampleSelective(@Param("record") T t, @Param("example") Example query);

    int updateByExample(@Param("record") T t, @Param("example") Example query);

    int deleteByPrimaryKey(ID id);

    int deleteByPrimaryKeys(List<ID> ids);

    int deleteByExample(Example query);

    boolean existsById(ID id);

    boolean existsByExample(Example query);

    default List<T> selectByPrimaryKeysWithSorted(List<ID> ids, Function<T, ID> apply) {
        List<T> ts = selectByPrimaryKeys(ids);
        if (apply != null && !ts.isEmpty()) {
            Map<ID, T> m = ts.stream()
                    .collect(Collectors.toMap(apply, Function.identity()));
            return ids.stream()
                    .map(m::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return ts;
    }


    default Map<ID, T> mapById(List<ID> ids, Function<T, ID> apply) {
        if (apply == null) {
            throw new IllegalArgumentException("Id convert can not be null!");
        }
        return selectByPrimaryKeys(ids)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(apply, Function.identity()));
    }

}
