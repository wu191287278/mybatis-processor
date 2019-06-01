package com.vcg.mybatis.example.starter;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import javax.persistence.OrderBy;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class MybatisQueryCreator extends AbstractQueryCreator<String, StringBuilder> {

    private static final Pattern FIND_PATTERN = Pattern.compile("^(findBy|getBy|queryBy|readBy|streamBy|cursorBy)");

    private static final Pattern FIND_ALL_PATTERN = Pattern.compile("^(streamAll|selectAll|streamAllBy|selectAllBy)");

    private static final Pattern COUNT_PATTERN = Pattern.compile("^(countBy)");

    private static final Pattern EXIST_PATTERN = Pattern.compile("^(existsBy)");

    private static final Pattern DELETE_PATTERN = Pattern.compile("^(removeBy|deleteBy)");

    private static final String FIND_SQL = "select <include refid=\"JqlBaseColumnList\" /> from <include refid=\"JqlTableName\" /> ";

    public static final String FIND_ALL_MAPPER_XML = "<SELECT id=\"%s\" resultMap=\"JqlBaseResultMap\">select <include refid=\"JqlBaseColumnList\" /> from <include refid=\"JqlTableName\" /> </SELECT>";

    private static final String DYNAMIC_COLUMN_FIND_SQL = "select %s from <include refid=\"JqlTableName\" /> ";

    private static final String COUNT_SQL = "select count(*) from <include refid=\"JqlTableName\" /> ";

    private static final String EXIST_SQl = "select 1 from  <include refid=\"JqlTableName\" /> ";

    private static final String DELETE_SQl = "delete from <include refid=\"JqlTableName\" />  ";

    private static final String SELECT_MAPPER_XML = "<select id=\"%s\" %s>%s</select>";

    private static final String DELETE_MAPPER_XML = "<delete id=\"%s\" %s>%s</select>";

    private Method method;

    private Map<String, String> columnMap;

    private String orderBy;

    public MybatisQueryCreator(PartTree tree, Method method, Map<String, String> columnMap) {
        super(tree);
        this.method = method;
        this.columnMap = columnMap;
        OrderBy orderByAnnotation = method.getAnnotation(OrderBy.class);
        this.orderBy = orderByAnnotation == null ? null : orderByAnnotation.value();
    }


    @Override
    protected StringBuilder create(Part part, Iterator<Object> iterator) {
        String condition = nextCondition(part);
        return new StringBuilder(condition);
    }

    @Override
    protected StringBuilder and(Part part, StringBuilder base, Iterator<Object> iterator) {
        String condition = nextCondition(part);
        return base.append(" and ")
                .append(condition);
    }

    @Override
    protected StringBuilder or(StringBuilder base, StringBuilder criteria) {
        return new StringBuilder("(")
                .append(base)
                .append(")")
                .append(" or ")
                .append("(")
                .append(criteria)
                .append(")");
    }

    @Override
    protected String complete(StringBuilder criteria, Sort sort) {

        if (this.orderBy != null || !sort.isEmpty()) {
            criteria.append(" order by ");
        }

        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                String columnName = this.columnMap.get(property);
                String name = order.getDirection().name();
                criteria.append(columnName)
                        .append(" ")
                        .append(name.toLowerCase())
                        .append(",");
            }
            criteria = new StringBuilder(criteria.substring(0, criteria.length() - 1));
        }

        if (this.orderBy != null) {
            criteria.append(this.orderBy);
        }

        String sql = FIND_SQL;

        IncludeColumns includeColumns = this.method.getAnnotation(IncludeColumns.class);
        if (includeColumns != null && includeColumns.value().length > 0) {
            sql = String.format(DYNAMIC_COLUMN_FIND_SQL, String.join(",", includeColumns.value()));
        }

        String resultType = "resultMap=\"JqlBaseResultMap\"";
        if (COUNT_PATTERN.matcher(this.method.getName()).find()) {
            sql = COUNT_SQL;
            resultType = "resultType=\"long\"";
        }

        if (method.getReturnType() == PageRequest.class) {
            resultType = "resultType=\"" + PageRequest.class.getName() + "\"";
        }


        if (EXIST_PATTERN.matcher(this.method.getName()).find()) {
            resultType = "resultType=\"boolean\"";
            sql = (EXIST_SQl + " where " + criteria.toString());
            return String.format(SELECT_MAPPER_XML, this.method.getName(), resultType, sql) + "limit 1";
        }

        if (DELETE_PATTERN.matcher(this.method.getName()).find()) {
            resultType = "resultType=\"int\"";
            sql = (DELETE_SQl + " where " + criteria.toString());
            return String.format(DELETE_MAPPER_XML, this.method.getName(), resultType, sql);
        }


        sql = (sql + " where " + criteria.toString());

        return String.format(SELECT_MAPPER_XML, this.method.getName(), resultType, sql);
    }

    private int numberOfArguments = 0;

    private String nextCondition(Part part) {
        PropertyPath property = part.getProperty();
        String segment = property.getSegment();
        String propertyName = "param" + (++numberOfArguments);
        String columnName = this.columnMap.get(segment);
        Part.Type type = part.getType();
        String Prefix = " " + columnName;

        if ("IN".equals(type.name())) {
            return Prefix + " in (<foreach  collection=\"" + propertyName + "\" item=\"item\" separator=\",\">#{item}</foreach>) ";
        }

        if ("NOT_IN".equals(type.name())) {
            return Prefix + " not in (<foreach  collection=\"" + propertyName + "\" item=\"item\" separator=\",\">#{item}</foreach>) ";
        }

        if ("EXISTS".equals(type.name())) {
            return Prefix + " exists (<foreach  collection=\"" + propertyName + "\" item=\"item\" separator=\",\">#{item}</foreach>) ";
        }

        if ("NOT_EXISTS".equals(type.name())) {
            return Prefix + " not exists (<foreach  collection=\"" + propertyName + "\" item=\"item\" separator=\",\">#{item}</foreach>) ";
        }

        if ("GREATER_THAN".equals(type.name()) || "AFTER".equals(type.name())) {
            return Prefix + " &gt; #{" + propertyName + "} ";
        }

        if ("GREATER_THAN_EQUAL".equals(type.name())) {
            return Prefix + " &gt;= #{" + propertyName + "} ";
        }

        if ("LESS_THAN".equals(type.name()) || "BEFORE".equals(type.name())) {
            return Prefix + "  &lt; #{" + propertyName + "} ";
        }

        if ("LESS_EQUAL".equals(type.name())) {
            return Prefix + "  &lt;= #{" + propertyName + "} ";
        }

        if ("IS_NULL".equals(type.name())) {
            numberOfArguments--;
            return Prefix + "  is null";
        }

        if ("IS_NOT_NULL".equals(type.name())) {
            numberOfArguments--;
            return Prefix + " is not null";
        }

        if ("TRUE".equals(type.name())) {
            numberOfArguments--;
            return Prefix + " = true";
        }

        if ("FALSE".equals(type.name())) {
            numberOfArguments--;
            return Prefix + " = false";
        }

        if ("STARTING_WITH".equals(type.name())) {
            return Prefix + " like '%#{" + propertyName + "}'";
        }

        if ("ENDING_WITH".equals(type.name())) {
            return Prefix + " like '#{" + propertyName + "}%'";
        }

        if ("LIKE".equals(type.name()) || "CONTAINS".equals(type.name())) {
            return Prefix + " like '#{" + propertyName + "}'";
        }

        if ("NOT_LIKE".equals(type.name()) || "NOT_CONTAINS".equals(type.name())) {
            return Prefix + " not like '#{" + propertyName + "}'";
        }

        if ("BETWEEN".equals(type.name())) {
            return Prefix + " between #{" + propertyName + "} and " + " #{param" + (++numberOfArguments) + "}";
        }

        if ("REGEX".equals(type.name())) {
            return Prefix + " regexp '#{" + propertyName + "}'";
        }

        return Prefix + " = #{" + propertyName + "} ";
    }


    public static boolean match(String methodName) {
        return FIND_PATTERN.matcher(methodName).find() ||
                COUNT_PATTERN.matcher(methodName).find() ||
                EXIST_PATTERN.matcher(methodName).find() ||
                DELETE_PATTERN.matcher(methodName).find();
    }

    public static boolean isSelectAll(String methodName) {
        return FIND_ALL_PATTERN.matcher(methodName).find();
    }

}
