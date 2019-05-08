package com.vcg.mybatis.example.starter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcg.mybatis.example.processor.handler.Separator;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericSeparatorTypeHandler implements TypeHandler<Object> {

    private Class<?> type;

    private Class genericType;

    private String delimiter;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);


    public GenericSeparatorTypeHandler(Field field) {
        this.type = field.getType();
        if (Collection.class.isAssignableFrom(type)) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                this.genericType = (Class) actualTypeArguments[0];
            }
        }
        this.delimiter = field.getAnnotation(Separator.class).value();
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null, jdbcType.TYPE_CODE);
            return;
        }

        if ("[]".equals(this.delimiter) || "{}".equals(this.delimiter)) {
            try {
                String value = OBJECT_MAPPER.writeValueAsString(parameter);
                ps.setObject(i, value, jdbcType.TYPE_CODE);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else if (parameter.getClass().isArray()) {
            ps.setObject(i, array2String((Object[]) parameter), jdbcType.TYPE_CODE);
        } else if (Collection.class.isAssignableFrom(type)) {
            ps.setObject(i, array2String((Collection<Object>) parameter), jdbcType.TYPE_CODE);
        } else {
            ps.setObject(i, parameter, jdbcType.TYPE_CODE);
        }

    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null) return null;

        return getResult(value);
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (value == null) return null;

        return getResult(value);
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (value == null) return null;
        return getResult(value);
    }

    private Object getResult(String value) {
        try {

            if ("{}".equals(this.delimiter)) {
                return OBJECT_MAPPER.readValue(value, type);
            }

            if ("[]".equals(this.delimiter)) {
                JavaType jt;
                if (Set.class.isAssignableFrom(this.type)) {
                    jt = OBJECT_MAPPER.getTypeFactory().constructParametricType(HashSet.class, this.genericType);
                } else {
                    jt = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, this.genericType);
                }
                return OBJECT_MAPPER.readValue(value, jt);
            }

            return string2Array(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Object string2Array(String value) {

        if (this.type.isArray()) {
            return convertArray(value);
        }

        Stream<Object> stream = Stream.of(value.split(this.delimiter))
                .map(this::convertValue);

        if (LinkedHashSet.class.isAssignableFrom(this.type)) {
            return stream.collect(Collectors.toCollection(LinkedHashSet::new));
        }

        if (LinkedList.class.isAssignableFrom(this.type)) {
            return stream.collect(Collectors.toCollection(LinkedList::new));
        }

        if (Set.class.isAssignableFrom(this.type)) {
            return stream.collect(Collectors.toSet());
        }

        return stream.collect(Collectors.toList());
    }

    private String array2String(Collection<Object> value) {
        return value.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(this.delimiter));
    }

    private String array2String(Object[] value) {
        return Stream.of(value)
                .map(String::valueOf)
                .collect(Collectors.joining(this.delimiter));
    }


    private Object convertValue(String value) {
        return convertValue(value, this.genericType);

    }

    private Object convertValue(String value, Class clazz) {
        if (clazz == null) return value;
        if (String.class.isAssignableFrom(clazz)) {
            return value;
        }

        if (Long.class.isAssignableFrom(clazz)) {
            return Long.valueOf(value);
        }

        if (Integer.class.isAssignableFrom(clazz)) {
            return Integer.valueOf(value);
        }

        if (Short.class.isAssignableFrom(clazz)) {
            return Short.valueOf(value);
        }

        if (Double.class.isAssignableFrom(clazz)) {
            return Double.valueOf(value);
        }

        if (Float.class.isAssignableFrom(clazz)) {
            return Float.valueOf(value);
        }

        if (Byte.class.isAssignableFrom(clazz)) {
            return Byte.valueOf(value);
        }

        if (Boolean.class.isAssignableFrom(clazz)) {
            return Boolean.valueOf(value);
        }

        return value;

    }


    private Object convertArray(String value) {
        String[] split = value.split(",");
        if (String[].class.isAssignableFrom(type)) {
            return split;
        }

        if (long[].class.isAssignableFrom(type)) {
            long[] items = new long[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Long.valueOf(split[i]);
            }
            return items;
        }

        if (Long[].class.isAssignableFrom(type)) {
            Long[] items = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Long.valueOf(split[i]);
            }
            return items;
        }

        if (int[].class.isAssignableFrom(type)) {
            int[] items = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Integer.valueOf(split[i]);
            }
            return items;
        }

        if (Integer[].class.isAssignableFrom(type)) {
            Integer[] items = new Integer[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Integer.valueOf(split[i]);
            }
            return items;
        }

        if (short[].class.isAssignableFrom(type)) {
            short[] items = new short[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Short.valueOf(split[i]);
            }
            return items;
        }

        if (Short[].class.isAssignableFrom(type)) {
            Short[] items = new Short[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Short.valueOf(split[i]);
            }
            return items;
        }

        if (double[].class.isAssignableFrom(type)) {
            double[] items = new double[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Double.valueOf(split[i]);
            }
            return items;
        }

        if (Double[].class.isAssignableFrom(type)) {
            Double[] items = new Double[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Double.valueOf(split[i]);
            }
            return items;
        }

        if (float[].class.isAssignableFrom(type)) {
            float[] items = new float[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Float.valueOf(split[i]);
            }
            return items;
        }

        if (Float[].class.isAssignableFrom(type)) {
            Float[] items = new Float[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Float.valueOf(split[i]);
            }
            return items;
        }

        if (Boolean[].class.isAssignableFrom(type)) {
            Boolean[] items = new Boolean[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Boolean.valueOf(split[i]);
            }
            return items;
        }

        if (boolean[].class.isAssignableFrom(type)) {
            boolean[] items = new boolean[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Boolean.valueOf(split[i]);
            }
            return items;
        }

        if (Byte[].class.isAssignableFrom(type)) {
            Byte[] items = new Byte[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Byte.valueOf(split[i]);
            }
            return items;
        }

        if (byte[].class.isAssignableFrom(type)) {
            byte[] items = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = Byte.valueOf(split[i]);
            }
            return items;
        }

        if (BigInteger[].class.isAssignableFrom(type)) {
            BigInteger[] items = new BigInteger[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = new BigInteger(split[i]);
            }
            return items;
        }

        if (BigDecimal[].class.isAssignableFrom(type)) {
            BigDecimal[] items = new BigDecimal[split.length];
            for (int i = 0; i < split.length; i++) {
                items[i] = new BigDecimal(split[i]);
            }
            return items;
        }

        if (Object[].class.isAssignableFrom(type)) {
            Object[] items = new Object[split.length];
            System.arraycopy(split, 0, items, 0, split.length);
            return items;
        }

        return split;
    }


}
