package com.vcg.mybatis.example.processor;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;

public interface MybatisExampleRepository<Domain, ID, Example> {

    Domain selectByPrimaryKey(ID id);

    List<Domain> selectByPrimaryKeys(List<ID> ids);

    List<Domain> selectByExample(Example example);

    Cursor selectByExampleWithCursor(Example example);

    int insert(Domain t);

    int insertSelective(Domain t);

    void insertBatch(List<Domain> ts);

    long countByExample(Example query);

    int updateByPrimaryKeySelective(Domain t);

    int updateByPrimaryKey(Domain t);

    int updateByExampleSelective(@Param("record") Domain t, @Param("example") Example query);

    int updateByExample(@Param("record") Domain t, @Param("example") Example query);

    void updateBatch(List<Domain> ts);

    int deleteByPrimaryKey(ID id);

    int deleteByPrimaryKeys(List<ID> ids);

}
