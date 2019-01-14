package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.CamelUtils;
import org.mybatis.spring.mapper.MapperFactoryBean;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Table(name = "users")
public class JpaMapperFactoryBean extends MapperFactoryBean<List> {

    private static final Pattern FIND_PATTERN = Pattern.compile("^(findBy|getBy|selectBy)");

    private static final Pattern COUNT_PATTERN = Pattern.compile("^(countBy)");

    private static final Pattern EXIST_PATTERN = Pattern.compile("^(existBy)");

    private Class<UserRepository> repositoryClass = UserRepository.class;

    enum Type {
        AND, OR, IN, NOT_IN, IS, EQUAL, NOT_EQUAL, IS_NULL, IS_NOT_NULL, IS_EMPTY, IS_NOT_EMPTY, LIKE, NOT_LIKE
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
    }

    public void wrapper() {
        Class domainClass = getDomainClass();
        if (domainClass == null) return;
        String tableName = getTableName();
        for (Method method : this.repositoryClass.getMethods()) {
            String name = method.getName();
            if (!FIND_PATTERN.matcher(name).find()) continue;
            StringBuilder sb = new StringBuilder("SELECT ")
                    .append(String.join(",", getColumns()))
                    .append(" FROM ")
                    .append(tableName)
                    .append(" WHERE ");
            List<String> keywords = CamelUtils.split(name.replaceAll(FIND_PATTERN.pattern(), ""), true);
            for (String keyword : keywords) {
                sb.append(keyword).append("=").append("#{" + keyword + "}").append(" ");
            }
            System.err.println(sb);
        }
    }


    private Class getReturnTypeName(Method method) {
        Class<?> returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType)) {
            return (Class) ((ParameterizedTypeImpl) method.getGenericReturnType()).getActualTypeArguments()[0];
        }
        return returnType;
    }

    private Class getDomainClass() {
        java.lang.reflect.Type[] genericSuperclass = this.repositoryClass.getGenericInterfaces();
        if (genericSuperclass != null && genericSuperclass.length > 0) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) genericSuperclass[0];
            return (Class) parameterizedType.getActualTypeArguments()[0];
        }
        return Object.class;
    }

    private String getTableName() {
        Table annotation = this.repositoryClass.getAnnotation(Table.class);
        if (annotation != null && !annotation.name().equals("")) {
            return annotation.name();
        }
        return CamelUtils.toSnake(getDomainClass().getSimpleName());
    }


    private Set<String> getColumns() {
        Class domainClass = getDomainClass();
        Field[] declaredFields = domainClass.getDeclaredFields();
        Set<String> columns = new LinkedHashSet<>();
        for (Field field : declaredFields) {
            Column column = field.getAnnotation(Column.class);

            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) ||
                    Modifier.isFinal(modifiers) ||
                    Modifier.isTransient(modifiers) ||
                    field.getAnnotation(Transient.class) != null ||
                    field.getAnnotation(ManyToMany.class) != null ||
                    field.getAnnotation(OneToMany.class) != null ||
                    field.getAnnotation(ManyToOne.class) != null) {
                continue;
            }

            if (column != null && !column.name().equals("")) {
                columns.add(column.name());
            }
        }
        return columns;
    }

    public static void main(String[] args) {
        new JpaMapperFactoryBean().wrapper();
    }

}
