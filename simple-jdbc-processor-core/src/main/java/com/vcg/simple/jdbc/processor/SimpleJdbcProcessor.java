package com.vcg.simple.jdbc.processor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.vcg.simple.jdbc.processor.domain.ColumnMetadata;
import com.vcg.simple.jdbc.processor.domain.DialectMetadata;
import com.vcg.simple.jdbc.processor.domain.TableMetadata;
import com.vcg.simple.jdbc.processor.util.CamelUtils;
import com.vcg.simple.jdbc.processor.visitor.DomainTypeVisitor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.persistence.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.vcg.simple.jdbc.processor.SimpleJdbc")
public class SimpleJdbcProcessor extends AbstractProcessor {

    private final DomainTypeVisitor domainTypeVisitor = new DomainTypeVisitor();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Filer filer = processingEnv.getFiler();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SimpleJdbc.class);
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
            ClassLoader classLoader = SimpleJdbcProcessor.class.getClassLoader();

            for (Element element : elements) {
                try {
                    TableMetadata tableMetadata = readTableMetadata(element);
                    String exampleName = tableMetadata.getExampleClazzName();

                    JavaFileObject javaFileObject = filer.createSourceFile(exampleName);
                    HashMap<String, Object> scopes = new HashMap<>();
                    scopes.put("metadata", tableMetadata);

                    SimpleJdbc example = element.getAnnotation(SimpleJdbc.class);
                    DialectMetadata dialect = example.dialect().getValue();

                    InputStream exampleInputStream = classLoader.getResourceAsStream(dialect.getExampleJavaTemplatePath());
                    try (InputStreamReader in = new InputStreamReader(exampleInputStream, StandardCharsets.UTF_8); Writer writer = javaFileObject.openWriter()) {
                        Mustache mustache = mf.compile(in, exampleName);
                        mustache.execute(writer, scopes);
                    }
                    String repositoryName = tableMetadata.getRepositoryClazzName();
                    JavaFileObject repositoryjavaFileObject = filer.createSourceFile(repositoryName);

                    InputStream repositoryInputstream = classLoader.getResourceAsStream(dialect.getRepositoryTemplatePath());
                    try (InputStreamReader in = new InputStreamReader(repositoryInputstream, StandardCharsets.UTF_8); Writer writer = repositoryjavaFileObject.openWriter()) {
                        Mustache mustache = mf.compile(in, repositoryName);
                        mustache.execute(writer, scopes);
                    }


                    if (example.shardTable()) {
                        String shardRepositoryName = tableMetadata.getShardRepositoryClazzName();
                        JavaFileObject shardRepositoryjavaFileObject = filer.createSourceFile(shardRepositoryName);

                        InputStream shardRepositoryInputstream = classLoader.getResourceAsStream(dialect.getShardRepositoryTemplatePath());
                        try (InputStreamReader in = new InputStreamReader(shardRepositoryInputstream, StandardCharsets.UTF_8); Writer writer = shardRepositoryjavaFileObject.openWriter()) {
                            Mustache mustache = mf.compile(in, shardRepositoryName);
                            mustache.execute(writer, scopes);
                        }
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


    public TableMetadata readTableMetadata(Element element) throws IOException {
        SimpleJdbc example = element.getAnnotation(SimpleJdbc.class);
        Table table = element.getAnnotation(Table.class);
        PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(element);
        String clazzName = getPackageName(element);

        String exampleName = (clazzName + "Example");

        DialectMetadata dialect = example.dialect().getValue();
        TableMetadata tableMetadata = new TableMetadata()
                .setDomainClazzName(clazzName)
                .setExampleClazzName(exampleName)
                .setPackageName(getPackageName(packageOf))
                .setLeftEncode(dialect.getLeftEscape())
                .setRightEncode(dialect.getRightEscape())
                .setUseSpring(example.useSpring())
                .setShard(null)
                .setSlaveDataSources(Arrays.asList(example.slaveDataSources()))
                .setDataSource(example.dataSource() == null || example.dataSource().isEmpty() ? null : example.dataSource());

        String repositoryName = clazzName + "SimpleJdbcRepository";
        String shardRepositoryClassName = clazzName + "ShardSimpleJdbcRepository";

        tableMetadata.setRepositoryClazzName(repositoryName)
                .setShardRepositoryClazzName(shardRepositoryClassName)
                .setTableName(table != null ? table.name() : String.join("_",
                        CamelUtils.split(tableMetadata.getDomainClazzSimpleName(), true)));
        tableMetadata.setOriginTableName(tableMetadata.getTableName());
        Elements elementUtils = processingEnv.getElementUtils();


        Set<String> keywords = new HashSet<>();
        ClassLoader classLoader = SimpleJdbcProcessor.class.getClassLoader();
        try (InputStream in = classLoader.getResourceAsStream("templates/keywords.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                keywords.add(line.toLowerCase());
            }
        }


        if (keywords.contains(tableMetadata.getTableName().toLowerCase())) {
            String encodeTable = tableMetadata.getLeftEncode() + tableMetadata.getTableName() + tableMetadata.getRightEncode();
            tableMetadata.setTableName(encodeTable);
        }

        for (Element member : element.getEnclosedElements()) {
            if (member.getModifiers().contains(Modifier.STATIC) || !member.getKind().isField() ||
                    member.getAnnotation(Transient.class) != null ||
                    member.getAnnotation(ManyToOne.class) != null) {
                continue;
            }

            String name = member.toString();

            Id id = member.getAnnotation(Id.class);
            Column column = member.getAnnotation(Column.class);
            GeneratedValue generatedValue = member.getAnnotation(GeneratedValue.class);
            Version version = member.getAnnotation(Version.class);
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

            if (column != null) {
                columnMetadata.setInsertable(column.insertable());
                columnMetadata.setUpdatable(column.updatable());
                columnMetadata.setVersion(version != null);
            }

            if (id != null) {
                tableMetadata.setPrimaryMetadata(columnMetadata);
            }

            Convert annotation = member.getAnnotation(Convert.class);
            if (annotation != null) {
                String typeHandler = annotation.attributeName();
                if (typeHandler.isEmpty()) {
                    try {
                        typeHandler = annotation.converter().getName();
                    } catch (MirroredTypeException mirroredTypeException) {
                        String errorMessage = mirroredTypeException.getLocalizedMessage();
                        typeHandler = errorMessage.substring(errorMessage.lastIndexOf(" ") + 1);
                    }
                }
                columnMetadata.setTypeHandler(typeHandler);
            }


            columnMetadata.setOriginColumnName(columnMetadata.getColumnName());

            String columnName = columnMetadata.getColumnName();
            if (keywords.contains(columnName)) {
                columnMetadata.setColumnName(tableMetadata.getLeftEncode() + columnName + tableMetadata.getRightEncode());
            }
            String docComment = elementUtils.getDocComment(member);
            columnMetadata.setJavaDoc(docComment);
            tableMetadata.getColumnMetadataList().add(columnMetadata);
        }

        if (tableMetadata.getPrimaryMetadata() == null && !tableMetadata.getColumnMetadataList().isEmpty()) {
            tableMetadata.setPrimaryMetadata(tableMetadata.getColumnMetadataList().get(0));
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


    public String getPackageName(Element packageOf) {
        String string = packageOf.toString();
        String[] split = string.split("\\s");
        return split.length == 1 ? split[0] : split[1];
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
