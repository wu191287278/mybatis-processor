package {{metadata.packageName}};

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class {{metadata.exampleClazzSimpleName}} implements Serializable {

    private static final long serialVersionUID = 314035125506252121L;

    public static final String DESC = " DESC";

    public static final String ASC = " ASC";

{{#metadata.columnMetadataList}}
    public static final String {{fieldName}} = "{{columnName}}";

{{/metadata.columnMetadataList}}

    private List<String> columns;

    private List<Integer> limit;

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public {{metadata.exampleClazzSimpleName}} withLimit(Integer... limit) {
        withLimit(Arrays.asList(limit));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withLimit(List<Integer> limit) {
        this.limit = limit;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withColumns(String... column) {
        this.limit = limit;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withOrderByClause(String orderByClause) {
        setOrderByClause(orderByClause);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withCriteria(Criteria c) {
        getOredCriteria().add(c);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withOrCriteria(Criteria c) {
        getOredCriteria().add(c);
        return this;
    }

    public static {{metadata.exampleClazzSimpleName}}.Criteria newCriteria() {
        return new Criteria();
    }


    public {{metadata.exampleClazzSimpleName}} withDistinct(boolean distinct) {
        setDistinct(distinct);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} withPage(Integer page, Integer size) {
        page = (page == null) ? 1 : page;
        page = (page <= 0) ? 1 : page;
        size = (size == null) ? 10 : size;
        size = (size <= 0) ? 10 : size;
        return withLimit(Arrays.asList((page - 1) * size, size));
    }

    public List<String> getColumns() {
        return columns;
    }

    public {{metadata.exampleClazzSimpleName}}() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
        this.limit = null;
        this.columns = null;
    }

    protected abstract static class GeneratedCriteria implements Serializable {

        private static final long serialVersionUID = 1543541368793026835L;

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }
{{#metadata.columnMetadataList}}
        public Criteria and{{firstUpFiledName}}IsNull() {
            addCriterion("{{columnName}} is null");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}IsNotNull() {
            addCriterion("{{columnName}} is not null");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}EqualTo({{javaType}} value) {
            addCriterion("{{columnName}} =", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}NotEqualTo({{javaType}} value) {
            addCriterion("{{columnName}} <>", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}In(List<{{javaType}}> values) {
            addCriterion("{{columnName}} in", values, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}NotIn(List<{{javaType}}> values) {
            addCriterion("{{columnName}} not in", values, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}Between({{javaType}} value1, {{javaType}} value2) {
            addCriterion("{{columnName}} between", value1, value2, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}NotBetween({{javaType}} value1, {{javaType}} value2) {
            addCriterion("{{columnName}} not between", value1, value2, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}GreaterThan({{javaType}} value) {
            addCriterion("{{columnName}} >", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}GreaterThanOrEqualTo({{javaType}} value) {
            addCriterion("{{columnName}} >=", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}LessThan({{javaType}} value) {
            addCriterion("{{columnName}} <", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}LessThanOrEqualTo({{javaType}} value) {
            addCriterion("{{columnName}} <=", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}Like({{javaType}} value) {
            addCriterion("{{columnName}} like", value, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFiledName}}NotLike({{javaType}} value) {
            addCriterion("{{columnName}} not like", value, "{{fieldName}}");
            return (Criteria) this;
        }
{{/metadata.columnMetadataList}}

    }

    public static class Criteria extends GeneratedCriteria implements Serializable {

        private static final long serialVersionUID = 9185867838086944489L;


        protected Criteria() {
            super();
        }
    }

    public static class Criterion implements Serializable {

        private static final long serialVersionUID = 5502500909305190410L;

        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }

}