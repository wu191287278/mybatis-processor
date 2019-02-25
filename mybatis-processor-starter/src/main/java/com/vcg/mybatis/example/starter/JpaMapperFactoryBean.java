package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.CamelUtils;
import com.vcg.mybatis.example.processor.MybatisExampleRepository;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.data.repository.query.parser.PartTree;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JpaMapperFactoryBean<T> extends MapperFactoryBean<T> {

    private static final String PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
            "<mapper namespace=\"%s\">%s</mapper>";


    public JpaMapperFactoryBean() {
    }

    public JpaMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        if (MybatisCrudRepository.class == getMapperInterface() || MybatisExampleRepository.class == getMapperInterface()) {
            return;
        }
        if (MybatisCrudRepository.class.isAssignableFrom(getMapperInterface())) {
            parseInterface();
        }
    }

    private void parseInterface() {
        Configuration configuration = getSqlSession().getConfiguration();
        Map<String, XNode> sqlFragments = configuration.getSqlFragments();
        List<String> queries = new ArrayList<>();
        Class domainClass = getDomainClass();
        Class<T> mapperInterface = getMapperInterface();
        Set<String> ignoreMethods = new HashSet<>();
        for (Method method : MybatisExampleRepository.class.getMethods()) {
            ignoreMethods.add(method.getName());
        }
        for (Method method : getMapperInterface().getMethods()) {
            if (ignoreMethods.contains(method.getName())) continue;
            Select select = method.getAnnotation(Select.class);
            Delete delete = method.getAnnotation(Delete.class);
            Update update = method.getAnnotation(Update.class);
            Insert insert = method.getAnnotation(Insert.class);
            SelectProvider selectProvider = method.getAnnotation(SelectProvider.class);
            DeleteProvider deleteProvider = method.getAnnotation(DeleteProvider.class);
            UpdateProvider updateProvider = method.getAnnotation(UpdateProvider.class);
            InsertProvider insertProvider = method.getAnnotation(InsertProvider.class);

            if (select != null || delete != null || update != null || insert != null) continue;
            if (selectProvider != null || deleteProvider != null || updateProvider != null || insertProvider != null) continue;

            Collection<String> mappedStatementNames = configuration.getMappedStatementNames();
            if (mappedStatementNames.contains(this.getMapperInterface().getName() + "." + method.getName())) continue;

            if (MybatisQueryCreator.match(method.getName())) {
                PartTree tree = new PartTree(method.getName(), domainClass);
                MybatisQueryCreator mybatisQueryCreator = new MybatisQueryCreator(
                        tree,
                        method,
                        getColumns(domainClass)
                );
                String queryXml = mybatisQueryCreator.createQuery();
                queries.add(queryXml);
            }
        }

        if (queries.size() > 0) {
            String mapper = String.format(PREFIX, mapperInterface.getName(), String.join("\r\n", queries));
            try (InputStream in = new ByteArrayInputStream(mapper.getBytes(StandardCharsets.UTF_8))) {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in,
                        configuration,
                        domainClass.getName() + "MybatisQueryCreator",
                        sqlFragments,
                        mapperInterface.getName()
                );
                xmlMapperBuilder.parse();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }


    private Map<String, String> getColumns(Class domainClass) {
        Field[] declaredFields = domainClass.getDeclaredFields();
        Map<String, String> columns = new LinkedHashMap<>();
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
                columns.put(field.getName(), column.name());
            } else {
                columns.put(field.getName(), CamelUtils.toSnake(field.getName()));
            }
        }
        return columns;
    }

    private Class getDomainClass() {
        java.lang.reflect.Type[] genericSuperclass = getMapperInterface().getGenericInterfaces();
        if (genericSuperclass != null && genericSuperclass.length > 0) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) genericSuperclass[0];
            return (Class) parameterizedType.getActualTypeArguments()[0];
        }
        return Object.class;
    }

}
