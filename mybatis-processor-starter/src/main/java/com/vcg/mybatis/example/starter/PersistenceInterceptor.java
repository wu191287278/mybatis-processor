package com.vcg.mybatis.example.starter;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
public class PersistenceInterceptor implements Interceptor {

    private Map<Class, Set<Field>> fieldCache = new ConcurrentHashMap<>();

    private Map<Class, Boolean> ignoreClass = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        setDateValue(parameter, ms);
        return invocation.proceed();
    }

    private void setDateValue(Object parameter, MappedStatement ms) throws IllegalAccessException {
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
            if (parameter instanceof List) {
                for (Object value : ((List) parameter)) {
                    setDateValue(value, ms);
                }
            } else if (parameter instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) parameter).entrySet()) {
                    setDateValue(entry.getValue(), ms);
                }
            } else {
                if (parameter != null) {
                    Class<?> parameterClass = parameter.getClass();
                    if (ignoreClass.containsKey(parameterClass)) return;
                    if (parameterClass.getAnnotation(Entity.class) == null || parameterClass.getAnnotation(Table.class) == null) {
                        ignoreClass.put(parameterClass, true);
                        return;
                    }

                    Set<Field> fields = fieldCache.get(parameterClass);
                    if (fields == null) {
                        fields = new HashSet<>();
                        while (parameterClass != null) {
                            for (Field field : parameterClass.getDeclaredFields()) {
                                CreatedDate createdDate = field.getAnnotation(CreatedDate.class);
                                LastModifiedDate lastModifiedDate = field.getAnnotation(LastModifiedDate.class);
                                if (createdDate == null && lastModifiedDate == null) continue;
                                if (Date.class.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType())) {
                                    field.setAccessible(true);
                                    fields.add(field);
                                }
                            }
                            parameterClass = parameterClass.getSuperclass();
                        }
                        fieldCache.put(parameter.getClass(), fields);
                    }

                    for (Field field : fields) {
                        Object value = field.get(parameter);
                        if (value == null) {
                            LastModifiedDate lastModifiedDate = field.getAnnotation(LastModifiedDate.class);
                            if (lastModifiedDate != null && sqlCommandType == SqlCommandType.UPDATE) {
                                field.set(parameter, currentDate(field));
                                continue;
                            }

                            CreatedDate createdDate = field.getAnnotation(CreatedDate.class);
                            if (createdDate != null && sqlCommandType == SqlCommandType.INSERT) {
                                field.set(parameter, currentDate(field));
                            }
                        }
                    }
                }

            }

        }
    }

    private Object currentDate(Field field) {
        if (Long.class.isAssignableFrom(field.getType())) {
            return System.currentTimeMillis();
        }

        return new Date();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


}
