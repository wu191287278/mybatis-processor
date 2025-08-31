package {{metadata.packageName}};

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class {{metadata.exampleClazzSimpleName}} implements java.io.Serializable {

    private static final long serialVersionUID = 1000000L;

    private static final List<String> COLUMNS = new ArrayList<>();

    private List<Integer> limit;

    private String orderByClause;

    private boolean distinct;

    private final String table = "{{metadata.tableName}}";

    private List<List<Criteria>> orConditions;

    private List<String> columns;

    private List<String> updateExpression;

    private List<Object> updateSetValues;

    private List<Criteria> criteries = new ArrayList<>();

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
        this.orConditions.add(this.criteries);
        this.criteries = new ArrayList<>();
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
        criteries.add(new Criteria(null, expression, value));
        return this;
    }
{{#metadata.columnMetadataList}}

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}IsNull() {
        criteries.add(new Criteria("{{columnName}}", " is null "));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}IsNotNull() {
        criteries.add(new Criteria("{{columnName}}", " is not null "));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}EqualTo({{javaType}} value) {
        criteries.add(new Criteria("{{columnName}}", " = ", value));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotEqualTo({{javaType}} value) {
        criteries.add(new Criteria("{{columnName}}", " <> ", value));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}In(List<{{javaType}}> values) {
        criteries.add(new Criteria("{{columnName}}", " in ", values));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}In({{javaType}}... values) {
        and{{firstUpFieldName}}In(Arrays.asList(values));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotIn(List<{{javaType}}> values) {
        criteries.add(new Criteria("{{columnName}}", " not in ", values));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotIn({{javaType}}... values) {
        and{{firstUpFieldName}}NotIn(Arrays.asList(values));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}Between({{javaType}} value1, {{javaType}} value2) {
        criteries.add(new Criteria("{{columnName}}", " between ", value1, value2));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotBetween({{javaType}} value1, {{javaType}} value2) {
        criteries.add(new Criteria("{{columnName}}", " not between ", value1, value2));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}GreaterThan({{javaType}} value) {
        criteries.add(new Criteria("{{columnName}}", " > ", value));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}GreaterThanOrEqualTo({{javaType}} value) {
        criteries.add(new Criteria("{{columnName}}", " >= ", value));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}LessThan({{javaType}} value) {
        criteries.add(new Criteria("{{columnName}}", " < ", value));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}LessThanOrEqualTo({{javaType}} value) {
        criteries.add(new Criteria("{{columnName}}", " <= ", value));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}Like(String value) {
        criteries.add(new Criteria("{{columnName}}", " like ", value));
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


    public List<List<Criteria>> getOrConditions() {
        return this.orConditions;
    }

    public List<Criteria> getCriteries() {
        return this.criteries;
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

    public List<Object> getUpdateSetValues() {
        return updateSetValues;
    }

    public List<String> getUpdateExpression() {
        return updateExpression;
    }


    public static class Criteria {

        private String column;
        private String condition;

        private Object value;

        private Object secondValue;

        private List listValue;


        public Criteria(String column, String condition) {
            this.column = column;
            this.condition = condition;
        }

        public Criteria(String column, String condition, Object value) {
            this.column = column;
            this.condition = condition;
            this.value = value;
        }

        public Criteria(String column, String condition, Object value, Object secondValue) {
            this.column = column;
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
        }

        public Criteria(String column, String condition, List listValue) {
            this.column = column;
            this.condition = condition;
            this.listValue = listValue;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public void setSecondValue(Object secondValue) {
            this.secondValue = secondValue;
        }

        public List getListValue() {
            return listValue;
        }

        public void setListValue(List<Object> listValue) {
            this.listValue = listValue;
        }
    }

}
