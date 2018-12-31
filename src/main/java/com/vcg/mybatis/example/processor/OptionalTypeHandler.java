package com.vcg.mybatis.example.processor;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OptionalTypeHandler extends BaseTypeHandler<Optional> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Optional parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public Optional getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Optional.empty();
    }


}
