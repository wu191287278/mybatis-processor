package com.vcg.mybatis.example.processor.parser;


import com.vcg.mybatis.example.processor.util.CamelUtils;

import java.util.regex.Pattern;

public class MybatisQueryCreator {

    private static final Pattern FIND_PATTERN = Pattern.compile("^(findBy|getBy|queryBy|readBy)");

    private static final Pattern COUNT_PATTERN = Pattern.compile("^(countBy)");

    private static final Pattern EXIST_PATTERN = Pattern.compile("^(existsBy)");

    private static final Pattern DELETE_PATTERN = Pattern.compile("^(removeBy|deleteBy)");

    private static final String KEYWORD_TEMPLATE = "(%s)(?=(\\p{Lu}|\\P{InBASIC_LATIN}))";

    private static final Pattern PREFIX_PATTERN = Pattern.compile(
            "^(findBy|getBy|queryBy|readBy|countBy|existsBy|removeBy|deleteBy)"
    );

    private static final String FIND_SQL = "select <include refid=\"Base_Column_List\" /> from <include refid=\"TABLE_NAME\" /> ";

    private static final String COUNT_SQL = "select count(*) from <include refid=\"TABLE_NAME\" /> ";

    private static final String EXIST_SQl = "select count(*) from  <include refid=\"TABLE_NAME\" /> ";

    private static final String DELETE_SQl = "delete from <include refid=\"TABLE_NAME\" />  ";

    private static final String SELECT_MAPPER_XML = "<select id=\"%s\" %s>%s</select>";

    private static final String DELETE_MAPPER_XML = "<delete id=\"%s\" %s>%s</select>";

    private MethodMetadata methodMetadata;

    public static String parse(MethodMetadata methodMetadata) {
        return new MybatisQueryCreator().setMethodMetadata(methodMetadata).parse();
    }

    private String parse() {
        String name = methodMetadata.getName();
        name = name.replaceAll(PREFIX_PATTERN.pattern(), "");
        String[] split = split(name, "Or");
        StringBuilder base = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            StringBuilder criteria = new StringBuilder();
            String[] andCriteria = split(split[i], "And");
            for (int j = 0; j < andCriteria.length; j++) {
                String criterion = andCriteria[j];
                Part.Type type = Part.Type.fromProperty(criterion);
                Part part = new Part(CamelUtils.toSnake(type.extractProperty(criterion)), type);
                if (j == 0) {
                    create(part, criteria);
                } else {
                    and(part, criteria);
                }
            }

            if (i > 0) {
                base = or(base, criteria);
            } else {
                base.append(criteria);
            }

        }

        return complete(base);
    }


    protected StringBuilder create(Part part, StringBuilder base) {
        String condition = nextCondition(part);
        return base.append(condition);
    }


    protected StringBuilder and(Part part, StringBuilder base) {
        String condition = nextCondition(part);
        return base.append(" and ")
                .append(condition);
    }

    protected StringBuilder or(StringBuilder base, StringBuilder criteria) {
        return new StringBuilder("(")
                .append(base)
                .append(")")
                .append(" or ")
                .append("(")
                .append(criteria)
                .append(")");
    }

    protected String complete(StringBuilder criteria) {

        String methodName = this.methodMetadata.getName();
        String sql = FIND_SQL;
        String resultType = "resultMap=\"BaseResultMap\"";
        if (COUNT_PATTERN.matcher(methodName).find()) {
            sql = COUNT_SQL;
            resultType = "resultType=\"long\"";
        }

        if (EXIST_PATTERN.matcher(methodName).find()) {
            resultType = "resultType=\"boolean\"";
            sql = (EXIST_SQl + " where " + criteria.toString());
            return String.format(SELECT_MAPPER_XML, methodName, resultType, sql);
        }

        if (DELETE_PATTERN.matcher(methodName).find()) {
            resultType = "resultType=\"int\"";
            sql = (DELETE_SQl + " where " + criteria.toString());
            return String.format(DELETE_MAPPER_XML, methodName, resultType, sql);
        }


        sql = (sql + " where " + criteria.toString());

        return String.format(SELECT_MAPPER_XML, methodName, resultType, sql);
    }

    private int numberOfArguments = 0;

    private String nextCondition(Part part) {
        String name = part.getName();
        String propertyName = "param" + (++numberOfArguments);
        Part.Type type = part.getType();
        String Prefix = " " + name;

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


    private static String[] split(String text, String keyword) {
        Pattern pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, keyword));
        return pattern.split(text);
    }

    private MybatisQueryCreator setMethodMetadata(MethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
        return this;
    }

    public static void main(String[] args) {
        String sql = parse(new MethodMetadata().setName("findByUsernameAndPasswordOrEmailAndAddress"));
        System.err.println(sql);
    }
}
