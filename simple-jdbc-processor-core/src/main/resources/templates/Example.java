package {{metadata.packageName}};

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class {{metadata.exampleClazzSimpleName}} implements Serializable {

    private static final long serialVersionUID = 1000000L;

    private static final List<String> COLUMNS = new ArrayList<>();

    private List<Integer> limit;

    private String orderByClause;

    private boolean distinct;

    private final String table = "{{metadata.tableName}}";

    private List<String> conditions = new ArrayList<>();

    private List<List<String>> orConditions;

    private List<Object> conditionValues = new ArrayList<>();

    private List<String> columns;

    private List<String> updateExpression;

    private List<Object> updateSetValues;


    public {{metadata.exampleClazzSimpleName}}() {}

    public static {{metadata.exampleClazzSimpleName}} create(){
        return new {{metadata.exampleClazzSimpleName}}();
    }
    
    public {{metadata.exampleClazzSimpleName}} distinct() {
        this.distinct = true;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} columns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} columns(String... columns) {
        this.columns = Arrays.asList(columns);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} ignoreColumns(List<String> ignoreColumns) {
        this.columns = new ArrayList<>(COLUMNS);
        this.columns.removeAll(ignoreColumns);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} ignoreColumns(String... ignoreColumns) {
        ignoreColumns(Arrays.asList(ignoreColumns));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} or() {
        if (this.orConditions == null) {
            this.orConditions = new ArrayList<>();
        }
        this.orConditions.add(this.conditions);
        this.conditions = new ArrayList<>();
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} set(String expression) {
        set(expression,null);
        return this;
    }
    /**
     * update expression.
     *
     * @param expression amount=amount+?
     * @param value      ? value
     */
    public {{metadata.exampleClazzSimpleName}} set(String expression, Object value) {
        if (updateSetValues == null) {
            this.updateSetValues = new ArrayList<>();
            this.updateExpression = new ArrayList<>();
        }
        this.updateExpression.add(expression);
        if (value != null) {
            this.updateSetValues.add(value);
        }
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and(String expression) {
        and(expression,null);
        return this;
    }
    /**
     * where expression
     *
     * @param expression amount > frozen+?
     * @param value      ?
     */
    public {{metadata.exampleClazzSimpleName}} and(String expression, Object value) {
        conditions.add(expression);
        if (value != null) {
            conditionValues.add(value);
        }
        return this;
    }
{{#metadata.columnMetadataList}}

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}IsNull() {
        conditions.add("{{columnName}} is null");
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}IsNotNull() {
        conditions.add("{{columnName}} is not null");
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}EqualTo({{javaType}} value) {
        conditions.add("{{columnName}} = ?");
        conditionValues.add(value);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotEqualTo({{javaType}} value) {
        conditions.add("{{columnName}} <> ?");
        conditionValues.add(value);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}In(List<{{javaType}}> values) {
        String placeholder = appendPlaceholder(values.size());
        conditions.add("{{columnName}} in " + placeholder);
        conditionValues.addAll(values);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}In({{javaType}}... values) {
        and{{firstUpFieldName}}In(Arrays.asList(values));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotIn(List<{{javaType}}> values) {
        String placeholder = appendPlaceholder(values.size());
        conditions.add("{{columnName}} not in " + placeholder);
        conditionValues.addAll(values);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotIn({{javaType}}... values) {
        and{{firstUpFieldName}}NotIn(Arrays.asList(values));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}Between({{javaType}} value1, {{javaType}} value2) {
        conditions.add("{{columnName}} between ? and  ? ");
        conditionValues.add(value1);
        conditionValues.add(value2);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotBetween({{javaType}} value1, {{javaType}} value2) {
        conditions.add("{{columnName}} not between ? and ? ");
        conditionValues.add(value1);
        conditionValues.add(value2);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}GreaterThan({{javaType}} value) {
        conditions.add("{{columnName}} > ?");
        conditionValues.add(value);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}GreaterThanOrEqualTo({{javaType}} value) {
        conditions.add("{{columnName}} >= ?");
        conditionValues.add(value);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}LessThan({{javaType}} value) {
        conditions.add("{{columnName}} < ?");
        conditionValues.add(value);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}LessThanOrEqualTo({{javaType}} value) {
        conditions.add("{{columnName}} <= ?");
        conditionValues.add(value);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}Like(String value) {
        conditions.add("{{columnName}} like ?");
        conditionValues.add(value);
        return this;
    }

{{/metadata.columnMetadataList}}

    public {{metadata.exampleClazzSimpleName}} orderBy(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} desc(String column) {
        this.orderByClause = column + " desc";
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} asc(String column) {
        this.orderByClause = column + " asc";
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} limit(Integer size) {
        this.limit = Collections.singletonList(size);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} limit(Integer start, Integer size) {
        this.limit = Arrays.asList(start, size);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} page(int page, int size) {
        if (page <= 0 || size <= 0 || page * size < 1) {
            throw new IllegalArgumentException("page or size for condition must greate 0");
        }
        this.limit = Arrays.asList((page - 1) * size, size);
        return this;
    }


    public List<List<String>> getOrConditions() {
        return orConditions;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public List<Object> getConditionValues() {
        return conditionValues;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public List<Integer> getLimit() {
        return limit;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public String getTable() {
        return this.table;
    }


    public static interface Column {
    {{#metadata.columnMetadataList}}
        public static final String {{fieldName}} = "{{columnName}}";
    {{/metadata.columnMetadataList}}
    }


    public static interface Field {
    {{#metadata.columnMetadataList}}
        public static final String {{fieldName}} = "{{fieldName}}";
    {{/metadata.columnMetadataList}}
    }

    static {
        {{#metadata.columnMetadataList}}
        COLUMNS.add("{{columnName}}");
        {{/metadata.columnMetadataList}}
    }

    private static String appendPlaceholder(int size) {
        StringBuilder sql = new StringBuilder();
        sql.append("(");
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                sql.append("?");
            } else {
                sql.append(", ?");
            }
        }
        sql.append(")");
        return sql.toString();
    }


    public List<Object> getUpdateSetValues() {
        return updateSetValues;
    }

    public List<String> getUpdateExpression() {
        return updateExpression;
    }
}
