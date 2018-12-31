package com.vcg.mybatis.example.processor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.persistence.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.vcg.mybatis.example.processor.Example")
public class MybatisDomainProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Filer filer = processingEnv.getFiler();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Example.class);

            MustacheFactory mf = new DefaultMustacheFactory();

            for (Element element : elements) {
                PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
                Example example = element.getAnnotation(Example.class);
                TableMetadata tableMetadata = read(element);
                String domainClazzName = tableMetadata.getDomainClazzName();
                String exampleName = (domainClazzName + "Example");

                tableMetadata.setRepositoryClazzName(example.namespace())
                        .setExampleClazzName(exampleName)
                        .setPackageName(packageOf.toString());

                JavaFileObject javaFileObject = filer.createSourceFile(exampleName);
                HashMap<String, Object> scopes = new HashMap<>();
                scopes.put("metadata", tableMetadata);

                ClassLoader classLoader = MybatisDomainProcessor.class.getClassLoader();

                InputStream exampleInputStream = classLoader.getResourceAsStream("templates/Example.java");
                try (InputStreamReader in = new InputStreamReader(exampleInputStream); Writer writer = javaFileObject.openWriter()) {
                    Mustache mustache = mf.compile(in, exampleName);
                    mustache.execute(writer, scopes);
                }


                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, packageOf.toString(), (tableMetadata.getDomainClazzSimpleName() + "ExampleMapper.xml"));
                InputStream xmlInputStream = classLoader.getResourceAsStream("templates/Example.xml");
                try (InputStreamReader in = new InputStreamReader(xmlInputStream);
                     Writer writer = resource.openWriter()) {
                    Mustache mustache = mf.compile(in, tableMetadata.getDomainClazzName() + ".xml");
                    mustache.execute(writer, scopes);
                }

            }
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, e.toString());
        }
        return true;
    }


    public TableMetadata read(Element element) {

        String clazzName = element.toString();

        Table table = element.getAnnotation(Table.class);

        TableMetadata tableMetadata = new TableMetadata()
                .setDomainClazzName(clazzName);
        tableMetadata.setTableName(table != null ? table.name() : String.join("_", CamelUtils.split(tableMetadata.getDomainClazzSimpleName(), true)));

        for (Element member : element.getEnclosedElements()) {
            boolean isStatic = member.getModifiers().stream().anyMatch(c -> Modifier.STATIC == c || Modifier.FINAL == c);
            if (isStatic || !member.getKind().isField() || member.getAnnotation(Transient.class) != null) {
                continue;
            }

            String name = member.toString();
            Id id = member.getAnnotation(Id.class);
            Column column = member.getAnnotation(Column.class);
            GeneratedValue generatedValue = member.getAnnotation(GeneratedValue.class);
            ColumnMetadata columnMetadata = new ColumnMetadata();
            member.asType().accept(new DomainTypeVisitor(), columnMetadata);
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
            tableMetadata.getColumnMetadataList().add(columnMetadata);

        }

        String columns = tableMetadata.getColumnMetadataList()
                .stream()
                .map(ColumnMetadata::getColumnName)
                .collect(Collectors.joining(","));

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
        JDBC_TYPE_MAPPING.put("DECIMAL UNSIGNED", "DOUBLE");
        JDBC_TYPE_MAPPING.put("TINY UNSIGNED", "TINY");
        JDBC_TYPE_MAPPING.put("TEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("TINYTEXT", "VARCHAR");
        JDBC_TYPE_MAPPING.put("MEDIUMTEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("LONGTEXT", "LONGVARCHAR");
        JDBC_TYPE_MAPPING.put("DATETIME", "DATE");
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
