package com.vcg.mybatis.example.processor.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class NumberEnumTypeHandler<E extends Enum> extends BaseTypeHandler<NumberEnum> {

    private final Class<NumberEnum<E>> type;

    private final Map<Long, NumberEnum<E>> m = new HashMap<>();

    public NumberEnumTypeHandler(Class<NumberEnum<E>> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        for (NumberEnum<E> e : type.getEnumConstants()) {
            m.put(e.getNumberValue().longValue(), e);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NumberEnum parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setLong(i, parameter.getNumberValue().longValue());
        } else {
            ps.setObject(i, parameter.getNumberValue().longValue(), jdbcType.TYPE_CODE); // see r3589
        }
    }

    @Override
    public NumberEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (rs.wasNull()) {
            return null;
        }
        return findEnum(rs.getLong(columnName));
    }

    @Override
    public NumberEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (rs.wasNull()) {
            return null;
        }
        return findEnum(rs.getLong(columnIndex));
    }

    @Override
    public NumberEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (cs.wasNull()) {
            return null;
        }
        return findEnum(cs.getLong(columnIndex));
    }

    private NumberEnum findEnum(long value) {
        NumberEnum<E> numberEnum = m.get(value);
        if (numberEnum == null) {
            throw new IllegalArgumentException("UnSupport enum type: " + value);
        }
        return numberEnum;
    }
}
