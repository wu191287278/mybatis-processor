package com.vcg.mybatis.example.starter;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.parser.PartTree;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.vcg.mybatis.example.processor.domain.ColumnMetadata;
import com.vcg.mybatis.example.processor.domain.JoinMetadata;
import com.vcg.mybatis.example.processor.domain.TableMetadata;
import com.vcg.mybatis.example.processor.util.CamelUtils;
import javax.persistence.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public class JqlParser {

    private static final Set<Method> IGNORE_METHODS = new HashSet<>(Arrays.asList(MybatisCrudRepository.class.getMethods()));

    private static final Set<String> PAGEABLE_RESULT_ID = new HashSet<>();

    private static final Set<String> STREAM_RESULT_ID = new HashSet<>();

    private static final Set<String> OPTIONAL_RESULT_ID = new HashSet<>();

    private static final Set<String> BOOLEAN_RESULT_ID = new HashSet<>();

    private static final Map<String, Method> METHODS = new HashMap<>();

    public static synchronized void parse(SqlSessionFactory sqlSessionFactory) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        for (Class<?> mapper : configuration.getMapperRegistry().getMappers()) {
            parse(configuration, mapper);
        }
    }


    public static synchronized void parse(Configuration configuration) {
        for (Class<?> mapper : configuration.getMapperRegistry().getMappers()) {
            parse(configuration, mapper);
        }
    }

    public static synchronized void parse(Configuration configuration, Class mapperInterface) {

        for (Method method : mapperInterface.getMethods()) {
            Class<?> returnType = method.getReturnType();
            if (returnType == Boolean.class || returnType == boolean.class) {
                String msId = mapperInterface.getName() + "." + method.getName();
                BOOLEAN_RESULT_ID.add(msId);
            }
        }
        if (!MybatisRepository.class.isAssignableFrom(mapperInterface)) {
            return;
        }

        Class domainClass = getDomainClass(mapperInterface);
        if (domainClass == Object.class) return;
        Set<String> queries = new HashSet<>();

        for (Method method : mapperInterface.getMethods()) {
            if (isIgnoreMethod(method)) continue;
            Select select = method.getAnnotation(Select.class);
            Delete delete = method.getAnnotation(Delete.class);
            Update update = method.getAnnotation(Update.class);
            Insert insert = method.getAnnotation(Insert.class);
            Transient aTransient = method.getAnnotation(Transient.class);
            SelectProvider selectProvider = method.getAnnotation(SelectProvider.class);
            DeleteProvider deleteProvider = method.getAnnotation(DeleteProvider.class);
            UpdateProvider updateProvider = method.getAnnotation(UpdateProvider.class);
            InsertProvider insertProvider = method.getAnnotation(InsertProvider.class);
            if (select != null || delete != null || update != null || insert != null || aTransient != null)
                continue;
            if (selectProvider != null || deleteProvider != null || updateProvider != null || insertProvider != null)
                continue;
            String msId = mapperInterface.getName() + "." + method.getName();

            Collection<String> mappedStatementNames = configuration.getMappedStatementNames();
            if (mappedStatementNames.contains(msId)) continue;

            if (MybatisQueryCreator.isSelectAll(method.getName())) {
                queries.add(String.format(MybatisQueryCreator.FIND_ALL_MAPPER_XML, method.getName()));
            } else if (MybatisQueryCreator.match(method.getName())) {
                PartTree tree = new PartTree(method.getName(), domainClass);
                MybatisQueryCreator mybatisQueryCreator = new MybatisQueryCreator(
                        tree,
                        method,
                        getColumns(domainClass)
                );
                String query = mybatisQueryCreator.createQuery();
                queries.add(query);

            }


            if (Page.class.isAssignableFrom(method.getReturnType())) {
                PAGEABLE_RESULT_ID.add(msId);
            }

            if (Stream.class.isAssignableFrom(method.getReturnType())) {
                STREAM_RESULT_ID.add(msId);
            }

            if (Optional.class.isAssignableFrom(method.getReturnType())) {
                OPTIONAL_RESULT_ID.add(msId);
            }

            METHODS.put(msId, method);

        }


        if (queries.size() > 0) {
            TableMetadata tableMetadata = read(configuration, domainClass, mapperInterface);
            HashMap<String, Object> scopes = new HashMap<>();
            scopes.put("metadata", tableMetadata);
            scopes.put("queries", queries);
            MustacheFactory mf = new DefaultMustacheFactory() {
                @Override
                public void encode(String value, Writer writer) {
                    try {
                        writer.write(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            };
            StringWriter writer = new StringWriter();
            Reader reader = new InputStreamReader(JqlParser.class.getClassLoader().getResourceAsStream("templates/JqlExample.xml"));
            Mustache mustache = mf.compile(reader, "Jql" + domainClass.getName());
            mustache.execute(writer, scopes);
            String mapper = writer.toString();
            try (InputStream in = new ByteArrayInputStream(mapper.getBytes(StandardCharsets.UTF_8))) {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in,
                        configuration,
                        domainClass.getName() + "MybatisQueryCreator",
                        configuration.getSqlFragments(),
                        mapperInterface.getName()
                );
                xmlMapperBuilder.parse();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private static Map<String, String> getColumns(Class domainClass) {
        Class superclass = domainClass;
        Map<String, String> columns = new LinkedHashMap<>();
        while (superclass != null) {
            Field[] declaredFields = superclass.getDeclaredFields();
            for (Field field : declaredFields) {
                Column column = field.getAnnotation(Column.class);
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) ||
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
            superclass = superclass.getSuperclass();
        }

        return columns;
    }

    private static Class getDomainClass(Class mapperInterface) {
        java.lang.reflect.Type[] genericSuperclass = mapperInterface.getGenericInterfaces();
        if (genericSuperclass != null && genericSuperclass.length > 0) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) genericSuperclass[0];
            return (Class) parameterizedType.getActualTypeArguments()[0];
        }
        return Object.class;
    }


    private static TableMetadata read(Configuration configuration, Class clazz, Class mapper) {
        Table table = (Table) clazz.getAnnotation(Table.class);
        String packageOf = clazz.getPackage().getName();

        String clazzName = clazz.getName();

        String exampleName = (clazzName + "Example");


        TableMetadata tableMetadata = new TableMetadata()
                .setDomainClazzName(clazzName)
                .setExampleClazzName(exampleName)
                .setPackageName(packageOf);

        String repositoryName = mapper.getName();
        tableMetadata.setRepositoryClazzName(repositoryName)
                .setTableName(table != null ? table.name() : String.join("_",
                        CamelUtils.split(tableMetadata.getDomainClazzSimpleName(), true)));

        for (Field member : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(member.getModifiers()) ||
                    member.getAnnotation(Transient.class) != null ||
                    member.getAnnotation(ManyToOne.class) != null) {
                continue;
            }


            String name = member.getName();
            JoinColumn joinColumn = member.getAnnotation(JoinColumn.class);
            OneToOne oneToOne = member.getAnnotation(OneToOne.class);
            OneToMany oneToMany = member.getAnnotation(OneToMany.class);
            ManyToMany manyToMany = member.getAnnotation(ManyToMany.class);

            if (joinColumn != null) {
                if (!"".equals(joinColumn.name())) {
                    JoinMetadata joinMetadata = new JoinMetadata()
                            .setColumnName(joinColumn.name())
                            .setFieldName(name);
                    if (oneToOne != null) {
                        joinMetadata.setMappedBy(oneToOne.mappedBy())
                                .setFetchType(oneToOne.fetch().name().toLowerCase());
                        tableMetadata.getOneToOne().add(joinMetadata);
                    }

                    if (oneToMany != null) {
                        joinMetadata.setMappedBy(oneToMany.mappedBy())
                                .setFetchType(oneToMany.fetch().name().toLowerCase());
                        tableMetadata.getOneToMany().add(joinMetadata);
                    }

                    if (manyToMany != null) {
                        joinMetadata.setMappedBy(manyToMany.mappedBy())
                                .setFetchType(manyToMany.fetch().name().toLowerCase());
                        tableMetadata.getOneToMany().add(joinMetadata);
                    }
                }
                continue;
            }
            Id id = member.getAnnotation(Id.class);
            Column column = member.getAnnotation(Column.class);
            GeneratedValue generatedValue = member.getAnnotation(GeneratedValue.class);
            ColumnMetadata columnMetadata = new ColumnMetadata();
            columnMetadata.setFieldName(name)
                    .setJavaType(member.getType().getName())
                    .setUseGeneratedKeys(generatedValue != null)
                    .setPrimary(id != null);


            if (column == null || "".equals(column.name())) {
                columnMetadata.setColumnName(String.join("_", CamelUtils.split(name, true)));
            }

            if (column != null && !"".equals(column.columnDefinition())) {
                String jdbcType = JDBC_TYPE_MAPPING.get(column.columnDefinition().replaceAll("\\s+", " ").toUpperCase());
                columnMetadata.setJdbcType(jdbcType != null ? jdbcType : column.columnDefinition());
            }

            if (column != null && !"".equals(column.name())) {
                columnMetadata.setColumnName(column.name());
            }

            if (id != null) {
                tableMetadata.setPrimaryMetadata(columnMetadata);
            }

            tableMetadata.getColumnMetadataList().add(columnMetadata);

        }

        String columns = tableMetadata.getColumnMetadataList()
                .stream()
                .map(ColumnMetadata::getColumnName)
                .collect(Collectors.joining(", "));


        return tableMetadata.setColumns(columns);
    }


    private static final Map<String, String> JDBC_TYPE_MAPPING = new HashMap<>();

    static {
        JDBC_TYPE_MAPPING.put("INT", "INTEGER");
        JDBC_TYPE_MAPPING.put("INT UNSIGNED", "INTEGER");
        JDBC_TYPE_MAPPING.put("SMALLINT UNSIGNED", "SMALLINT");
        JDBC_TYPE_MAPPING.put("BIGINT UNSIGNED", "BIGINT");
        JDBC_TYPE_MAPPING.put("DOUBLE UNSIGNED", "DOUBLE");
        JDBC_TYPE_MAPPING.put("FLOAT UNSIGNED", "DOUBLE");
        JDBC_TYPE_MAPPING.put("DECIMAL UNSIGNED", "DECIMAL");
        JDBC_TYPE_MAPPING.put("TINY UNSIGNED", "TINY");
        JDBC_TYPE_MAPPING.put("TEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("TINYTEXT", "VARCHAR");
        JDBC_TYPE_MAPPING.put("MEDIUMTEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("LONGTEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("DATETIME", "TIMESTAMP");
    }

    private static final Set<String> IGNORE_METHOD_NAMES = Arrays.stream(MybatisCrudRepository.class.getMethods())
            .filter(method -> method.getReturnType() != Boolean.class && method.getReturnType() != boolean.class)
            .map(Method::getName)
            .collect(Collectors.toSet());


    public static boolean isIgnoreMethod(Method method) {
        return IGNORE_METHODS.contains(method);
    }

    public static boolean isIgnoreMethod(String name) {
        if (name.lastIndexOf(".") != -1) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }
        return IGNORE_METHOD_NAMES.contains(name);
    }

    public static boolean isPageResultId(String msId) {
        return PAGEABLE_RESULT_ID.contains(msId);
    }


    public static boolean isStreamResultId(String msId) {
        return STREAM_RESULT_ID.contains(msId);
    }

    public static boolean isOptionalResultId(String msId) {
        return OPTIONAL_RESULT_ID.contains(msId);
    }

    public static boolean isBooleanResultId(String msId) {
        return BOOLEAN_RESULT_ID.contains(msId);
    }


}
