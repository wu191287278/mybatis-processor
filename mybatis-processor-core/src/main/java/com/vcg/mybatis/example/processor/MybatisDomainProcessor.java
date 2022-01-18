package com.vcg.mybatis.example.processor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.vcg.mybatis.example.processor.annotation.ExampleQuery;
import com.vcg.mybatis.example.processor.annotation.*;
import com.vcg.mybatis.example.processor.domain.*;
import com.vcg.mybatis.example.processor.util.CamelUtils;
import com.vcg.mybatis.example.processor.visitor.DomainTypeVisitor;
import com.vcg.mybatis.example.processor.visitor.QueryTypeVisitor;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.persistence.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.apache.ibatis.type.TypeHandler;

@SupportedAnnotationTypes("com.vcg.mybatis.example.processor.Example")
public class MybatisDomainProcessor extends AbstractProcessor {

    private final DomainTypeVisitor domainTypeVisitor = new DomainTypeVisitor();
    private final QueryTypeVisitor queryTypeVisitor = new QueryTypeVisitor();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Filer filer = processingEnv.getFiler();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Example.class);
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
            ClassLoader classLoader = MybatisDomainProcessor.class.getClassLoader();

            for (Element element : elements) {
                try {
                    TableMetadata tableMetadata = readTableMetadata(element);
                    String exampleName = tableMetadata.getExampleClazzName();

                    JavaFileObject javaFileObject = filer.createSourceFile(exampleName);
                    HashMap<String, Object> scopes = new HashMap<>();
                    scopes.put("metadata", tableMetadata);


                    InputStream exampleInputStream = classLoader.getResourceAsStream("templates/Example.java");
                    try (InputStreamReader in = new InputStreamReader(exampleInputStream, StandardCharsets.UTF_8); Writer writer = javaFileObject.openWriter()) {
                        Mustache mustache = mf.compile(in, exampleName);
                        mustache.execute(writer, scopes);
                    }

                    PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
                    String xml = tableMetadata.getDomainClazzSimpleName() + "ExampleMapper.xml";
                    FileObject xmlOut = filer.createResource(StandardLocation.CLASS_OUTPUT, packageOf.toString(), xml);
                    InputStream xmlInputStream = classLoader.getResourceAsStream("templates/Example.xml");
                    try (InputStreamReader in = new InputStreamReader(xmlInputStream, StandardCharsets.UTF_8);
                         Writer writer = xmlOut.openWriter()) {
                        Mustache mustache = mf.compile(in, tableMetadata.getDomainClazzName() + ".xml");
                        mustache.execute(writer, scopes);
                    }

                    Example example = element.getAnnotation(Example.class);

                    ExampleQuery exampleQuery = example.query();
                    if (!exampleQuery.enable()) {
                        continue;
                    }
                    QueryMetadata queryMetadata = readQueryMetadata(element);
                    String queryName = queryMetadata.getQueryClazzName();
                    JavaFileObject queryJavaFileObject = filer.createSourceFile(queryName);
                    scopes.put("query", queryMetadata);
                    InputStream queryInputStream = classLoader.getResourceAsStream("templates/Query.java");
                    try (InputStreamReader in = new InputStreamReader(queryInputStream, StandardCharsets.UTF_8);
                         Writer writer = queryJavaFileObject.openWriter();) {
                        Mustache mustache = mf.compile(in, queryName);
                        mustache.execute(writer, scopes);
                    }
                } catch (Exception e) {
                    try (StringWriter writer = new StringWriter();
                         PrintWriter printWriter = new PrintWriter(writer)) {
                        e.printStackTrace(printWriter);
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, writer.toString());
                    }
                }
            }


        } catch (Exception e) {
            try (StringWriter writer = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(writer)) {
                e.printStackTrace(printWriter);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, writer.toString());
            } catch (IOException ioException) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            }
        }
        return true;
    }


    public TableMetadata readTableMetadata(Element element) {
        Example example = element.getAnnotation(Example.class);
        Table table = element.getAnnotation(Table.class);
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        String clazzName = element.toString();

        String exampleName = (clazzName + "Example");

        TableMetadata tableMetadata = new TableMetadata()
                .setDomainClazzName(clazzName)
                .setExampleClazzName(exampleName)
                .setPackageName(packageOf.toString())
                .setShard(null);

        String repositoryName = !example.namespace().equals("") ? example.namespace() :
                exampleName + "." + tableMetadata.getExampleClazzSimpleName() + "Repository";
        tableMetadata.setRepositoryClazzName(repositoryName)
                .setTableName(table != null ? table.name() : String.join("_",
                        CamelUtils.split(tableMetadata.getDomainClazzSimpleName(), true)));
        Elements elementUtils = processingEnv.getElementUtils();

        for (Element member : element.getEnclosedElements()) {
            if (member.getModifiers().contains(Modifier.STATIC) || !member.getKind().isField() ||
                    member.getAnnotation(Transient.class) != null ||
                    member.getAnnotation(ManyToOne.class) != null) {
                continue;
            }

            String name = member.toString();
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
            member.asType().accept(domainTypeVisitor, columnMetadata);
            columnMetadata.setFieldName(name)
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

            Convert annotation = member.getAnnotation(Convert.class);
            if (annotation != null && TypeHandler.class.isAssignableFrom(annotation.converter())) {
                String typeHandler = annotation.converter().getName();
                columnMetadata.setTypeHandler(typeHandler);
            }

            String docComment = elementUtils.getDocComment(member);
            columnMetadata.setJavaDoc(docComment);
            tableMetadata.getColumnMetadataList().add(columnMetadata);
        }

        String columns = tableMetadata.getColumnMetadataList()
                .stream()
                .map(ColumnMetadata::getColumnName)
                .collect(Collectors.joining(", "));

        return tableMetadata.setColumns(columns);
    }


    public QueryMetadata readQueryMetadata(Element element) {
        Elements elementUtils = processingEnv.getElementUtils();

        PackageElement packageOf = elementUtils.getPackageOf(element);
        String packageName = packageOf.toString();
        String clazzName = element.toString();

        String queryName = (clazzName + "Query");
        String exampleName = (clazzName + "Example");
        QueryMetadata metadata = new QueryMetadata()
                .setQueryClazzName(queryName)
                .setExampleClazzName(exampleName)
                .setPackageName(packageName);
        Example example = element.getAnnotation(Example.class);
        ExampleQuery exampleQuery = example.query();

        CriteriaMetadata criteriaMetadata = new CriteriaMetadata();
        List<CriteriaMetadata> list = new ArrayList<>();
        List<CriteriaMetadata> orList = new ArrayList<>();
        for (Element member : element.getEnclosedElements()) {
            if (member.getModifiers().contains(Modifier.STATIC) || !member.getKind().isField() ||
                    member.getAnnotation(Transient.class) != null) {
                continue;
            }
            String name = member.toString();
            CriterionMetadata criterionMetadata = new CriterionMetadata();
            criterionMetadata.setSingle(true);
            Criterion criterion = member.getAnnotation(Criterion.class);
            if (criterion == null && !exampleQuery.all()) {
                continue;
            }
            if (criterion != null) {
                criterionMetadata.setFieldName("".equals(criterion.value()) ? name : criterion.value());
                if (criterion.equalTo()) {
                    criterionMetadata.setEqualTo(true);
                } else if (criterion.notEqualTo()) {
                    criterionMetadata.setNotEqualTo(true);
                } else if (criterion.greaterThan()) {
                    criterionMetadata.setGreaterThan(true);
                } else if (criterion.greaterThanOrEqualTo()) {
                    criterionMetadata.setGreaterThanOrEqualTo(true);
                } else if (criterion.in()) {
                    criterionMetadata.setIn(true);
                } else if (criterion.notIn()) {
                    criterionMetadata.setNotIn(true);
                } else if (criterion.lessThan()) {
                    criterionMetadata.setLessThan(true);
                } else if (criterion.lessThanOrEqualTo()) {
                    criterionMetadata.setLessThanOrEqualTo(true);
                } else if (criterion.like()) {
                    criterionMetadata.setLike(true);
                } else if (criterion.notLike()) {
                    criterionMetadata.setNotLike(true);
                } else if (criterion.between()) {
                    criterionMetadata.setBetween(true);
                } else {
                    criterionMetadata.setEqualTo(true);
                }

            } else {
                criterionMetadata.setFieldName(name);
                criterionMetadata.setSingle(true);
                criterionMetadata.setEqualTo(true);
            }


            member.asType().accept(queryTypeVisitor, criterionMetadata);


            if (criterionMetadata.getFieldName() == null) {
                continue;
            }
            String javaType = criterionMetadata.getJavaType();
            if (javaType.equals("Date") || javaType.equals("java.util.Date")) {
                criterionMetadata.setBetween(true);
                if (!exampleQuery.dateFormat().equals("")) {
                    criterionMetadata.setDateFormat(exampleQuery.dateFormat());
                    criterionMetadata.setNumberFormat(false);
                } else {
                    criterionMetadata.setJavaType("java.lang.Long");
                    criterionMetadata.setNumberFormat(true);
                }
            }

            String docComment = elementUtils.getDocComment(member);

            if (criterion != null && criterion.or().length > 1) {
                CriteriaMetadata orCriteriaMetadata = new CriteriaMetadata();
                orCriteriaMetadata.setOr(true);
                for (String n : criterion.or()) {
                    CriterionMetadata c = copyCriteria(criterionMetadata);
                    c.setFieldAliasName(n);
                    orCriteriaMetadata.getCriteria().add(c);
                    c.setJavaDoc(docComment);
                }
                orList.add(orCriteriaMetadata);
                continue;
            }
            criterionMetadata.setJavaDoc(docComment);
            criterionMetadata.setFieldAliasName(name);
            criteriaMetadata.getCriteria().add(criterionMetadata);
        }

        if (criteriaMetadata.getCriteria().size() > 0) {
            list.add(criteriaMetadata);
        }

        list.addAll(orList);

        if (list.size() > 1) {
            list.get(0).setOr(false);
        }

        metadata.setCriteria(list);
        metadata.setPage(exampleQuery.page().equals("") ? null : exampleQuery.page());
        metadata.setSize(exampleQuery.size().equals("") ? null : exampleQuery.size());
        metadata.setOrderBy(exampleQuery.orderBy().equals("") ? null : exampleQuery.orderBy());
        metadata.setSort(exampleQuery.sort().equals("") ? null : exampleQuery.sort());
        metadata.setSizeDefault(exampleQuery.sizeDefault() == 0 ? null : exampleQuery.sizeDefault());
        metadata.setPageDefault(exampleQuery.pageDefault() == 0 ? null : exampleQuery.pageDefault());
        metadata.setOrderByDefault(exampleQuery.orderByDefault().equals("") ? null : exampleQuery.orderByDefault());
        metadata.setSortDefault(exampleQuery.sortDefault().equals("") ? null : exampleQuery.sortDefault());
        return metadata;
    }

    private CriterionMetadata copyCriteria(CriterionMetadata criterionMetadata) {
        CriterionMetadata copy = new CriterionMetadata();
        copy.setFirstUpFieldName(criterionMetadata.getFirstUpFieldName());
        copy.setFieldName(criterionMetadata.getFieldName());
        copy.setJavaType(criterionMetadata.getJavaType());
        copy.setLike(criterionMetadata.isLike());
        copy.setNotLike(criterionMetadata.isNotLike());
        copy.setIn(criterionMetadata.isIn());
        copy.setNotIn(criterionMetadata.isNotIn());
        copy.setEqualTo(criterionMetadata.isEqualTo());
        copy.setNotEqualTo(criterionMetadata.isNotEqualTo());
        copy.setGreaterThan(criterionMetadata.isGreaterThan());
        copy.setLessThan(criterionMetadata.isLessThan());
        copy.setGreaterThanOrEqualTo(criterionMetadata.isGreaterThanOrEqualTo());
        copy.setLessThanOrEqualTo(criterionMetadata.isLessThanOrEqualTo());
        copy.setFieldAliasName(criterionMetadata.getFieldAliasName());
        copy.setFirstUpFieldAliasName(criterionMetadata.getFirstUpFieldAliasName());
        return copy;

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


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
