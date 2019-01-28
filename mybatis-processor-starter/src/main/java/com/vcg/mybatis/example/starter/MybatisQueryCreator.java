package com.vcg.mybatis.example.starter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class MybatisQueryCreator extends AbstractQueryCreator<String, StringBuilder> {

    private static final Pattern FIND_PATTERN = Pattern.compile("^(findBy|getBy|queryBy)");

    private static final Pattern COUNT_PATTERN = Pattern.compile("^(countBy)");

    private static final Pattern EXIST_PATTERN = Pattern.compile("^(existsBy)");

    private static final String FIND_SQL = "select <include refid=\"Base_Column_List\" /> from <include refid=\"TABLE_NAME\" /> ";

    private static final String COUNT_SQL = "select count(*) from <include refid=\"TABLE_NAME\" /> ";

    private static final String EXIST_SQl = "select if(sum(1),1,0) from <include refid=\"TABLE_NAME\" /> ";

    private static final String MAPPER_XML = "<select id=\"%s\" %s>%s</select>";

    private Method method;

    private Map<String, String> columnMap;

    public MybatisQueryCreator(PartTree tree, Method method, Map<String, String> columnMap) {
        super(tree);
        this.method = method;
        this.columnMap = columnMap;
    }


    @Override
    protected StringBuilder create(Part part, Iterator<Object> iterator) {
        String condition = getCondition(part);
        return new StringBuilder(condition);
    }

    @Override
    protected StringBuilder and(Part part, StringBuilder base, Iterator<Object> iterator) {
        String condition = getCondition(part);
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
        if (!sort.isEmpty()) {
            criteria.append(" order by ");
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                String name = order.getDirection().name();
                criteria.append(property)
                        .append(" ")
                        .append(name)
                        .append(" ");
            }
        }

        for (int i = 0; i < method.getParameterTypes().length; i++) {
            if (Pageable.class == method.getParameterTypes()[i]) {
                String param = "param" + (i + 1);
                criteria.append("<foreach open=\"order by\"  collection=\""+param+".sort\" item=\"item\" separator=\",\">${item.property} ${item.direction}</foreach> ")
                        .append(" limit ${(" + param + ".pageNumber-1)*" + param + ".pageSize}, ${" + param + ".pageSize}");
                break;
            }
        }


        String sql = FIND_SQL;
        String resultType = "resultMap=\"BaseResultMap\"";
        if (COUNT_PATTERN.matcher(this.method.getName()).find()) {
            sql = COUNT_SQL;
            resultType = "resultType=\"long\"";
        }

        if (EXIST_PATTERN.matcher(this.method.getName()).find()) {
            resultType = "resultType=\"boolean\"";
            sql = (EXIST_SQl + " where " + criteria.toString() + " limit 1");
            return String.format(MAPPER_XML, this.method.getName(), resultType, sql);
        }

        sql = (sql + " where " + criteria.toString());

        return String.format(MAPPER_XML, this.method.getName(), resultType, sql);
    }

    private int numberOfArguments = 0;

    private String getCondition(Part part) {
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
                EXIST_PATTERN.matcher(methodName).find();
    }

}
