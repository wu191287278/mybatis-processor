package {{metadata.packageName}};

import java.io.Serializable;
import java.util.*;
import java.math.*;


public class {{metadata.exampleClazzSimpleName}} implements Serializable {

    private static final long serialVersionUID = 1000000L;

    public static final String TABLE_NAME = "{{metadata.tableName}}";

    private static final String DESC = " DESC";

    private static final String ASC = " ASC";

    private static final Map<String,String> MAPPING = new HashMap<String,String>();

    private List<String> columns;

    private List<Integer> limit;

    private String orderByClause;

    private boolean distinct;

    private List<Criteria> oredCriteria;

    private Criteria currentCriteria;

    private Integer page;

    private Integer size;

    private String table = TABLE_NAME;

    private String groupByClause;


    public {{metadata.exampleClazzSimpleName}}() {}

    public static {{metadata.exampleClazzSimpleName}} create(){
        return new {{metadata.exampleClazzSimpleName}}();
    }

    public {{metadata.exampleClazzSimpleName}} copy(){
        {{metadata.exampleClazzSimpleName}} example = create()
                .setColumns(this.columns)
                .distinct(this.distinct)
                .orderByClause(this.orderByClause);
        example.setLimit(this.limit);
        example.setOredCriteria(this.oredCriteria);
        example.setPage(this.page);
        example.setSize(this.size);
        example.setTable(this.table);
        return example;
    }

    public {{metadata.exampleClazzSimpleName}} limit(int offset, int limit) {
        this.limit = Arrays.asList(offset,limit);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} limit(int limit) {
        this.limit = Arrays.asList(limit);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} orderBy(String name,String sort) {
        return orderByClause(MAPPING.get(name)+" "+sort);
    }

    public {{metadata.exampleClazzSimpleName}} orderBy(String name, boolean sort) {
        return orderBy(name,sort?DESC:ASC);
    }

    public {{metadata.exampleClazzSimpleName}} page(int page, int size) {
        if(page<=0 ||size <=0){
            throw new RuntimeException("page or size for condition must greate 0");
        }
        this.page = page;
        this.size = size;
        this.limit = Arrays.asList((page - 1) * size, size);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} columns(String... columns) {
        columns(Arrays.asList(columns));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} columns(List<String> columns){
        return setColumns(columns);
    }

    public {{metadata.exampleClazzSimpleName}} setColumns(List<String> columns) {
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            if(MAPPING.containsKey(column)){
                addColumn(MAPPING.get(column));
            }
            if(column.startsWith("sum(") ||column.startsWith("max(")
                    ||column.startsWith("min(") ||column.startsWith("count(")) {
                addColumn(columns.get(i));
            }
        }
        return this;
    }

    private void addColumn(String column){
        if(this.columns == null){
            this.columns = new ArrayList<>();
        }
        this.columns.add(column);
    }

    public {{metadata.exampleClazzSimpleName}} sum(String column) {
        sum(column,MAPPING.get(column));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} sumDistinct(String column) {
        sumDistinct(column,MAPPING.get(column));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} count(String column) {
        count(column,MAPPING.get(column));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} countDistinct(String column) {
        countDistinct(column,MAPPING.get(column));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} min(String column) {
        min(column,MAPPING.get(column));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} max(String column) {
        max(column,MAPPING.get(column));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} sum(String column,String alias) {
        addColumn("sum("+MAPPING.get(column)+") as "+alias);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} sumDistinct(String column,String alias) {
        addColumn("sum(distinct "+MAPPING.get(column)+") as "+alias);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} count(String column,String alias) {
        addColumn("count("+MAPPING.get(column)+") as "+alias);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} countDistinct(String column,String alias) {
        addColumn("count(distinct "+MAPPING.get(column)+") as "+alias);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} min(String column,String alias) {
        addColumn("min("+MAPPING.get(column)+") as "+alias);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} max(String column,String alias) {
        addColumn("max("+MAPPING.get(column)+") as "+alias);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} groupBy(String... columns) {
        columns(columns);
        List<String> groupByColumns = new ArrayList<>();
        for (String column : columns) {
            groupByColumns.add(MAPPING.get(column));
        }
        this.groupByClause = String.join(", " , groupByColumns);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} ignoreColumns(String... columns) {
        ignoreColumns(Arrays.asList(columns));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} ignoreColumns(List<String> columns) {
        if(this.columns==null || this.columns.isEmpty()){
            this.columns = allColumns();
        }
        for (String column : columns) {
            this.columns.remove(MAPPING.get(column));
        }
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} orderByClause(String orderByClause) {
        setOrderByClause(orderByClause);
        return this;
    }

    private {{metadata.exampleClazzSimpleName}} criteria(Criteria criteria) {
        getOredCriteria().add(criteria);
        this.currentCriteria = criteria;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} distinct(boolean distinct) {
        setDistinct(distinct);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} distinct() {
        setDistinct(true);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} asc(String column){
        return orderBy(column,ASC);
    }

    public {{metadata.exampleClazzSimpleName}} desc(String column){
        return orderBy(column,DESC);
    }

    public String getTable() {
        return this.table;
    }

    public {{metadata.exampleClazzSimpleName}} table(String table) {
        this.table = table;
        return this;
    }

    private void checkCriteria(){
        if(this.currentCriteria == null){
            throw new RuntimeException("criteria for condition cannot be null");
        }
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
        if (this.oredCriteria == null) {
            this.oredCriteria = new ArrayList<>();
        }
        for (Criteria oredCriterion : oredCriteria) {
            for (Criterion criterion : oredCriterion.getCriteria()) {
                if (criterion.isDateValue()) {
                    Object value = criterion.getValue();
                    if (value instanceof Long) {
                        criterion.setValue(new Date((Long) value));
                    }
                    Object secondValue = criterion.getSecondValue();
                    if (secondValue instanceof Long) {
                        criterion.setSecondValue(new Date((Long) secondValue));
                    }
                }
            }
        }
        return this.oredCriteria;
    }

    private {{metadata.exampleClazzSimpleName}} or(Criteria criteria) {
        getOredCriteria().add(criteria);
        this.currentCriteria = criteria;
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} or() {
        return or(createCriteriaInternal());
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        List<Criteria> cs = getOredCriteria();
        if (cs.size() == 0) {
            cs.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        this.oredCriteria.clear();
        this.orderByClause = null;
        this.distinct = false;
        this.limit = null;
        this.columns = null;
        this.page = null;
        this.size = null;
        this.table = null;
        this.currentCriteria = null;
        this.table = null;
    }

{{#metadata.columnMetadataList}}
    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}IsNull() {
        getCriteria().and{{firstUpFieldName}}IsNull();
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}IsNotNull() {
        getCriteria().and{{firstUpFieldName}}IsNotNull();
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}EqualTo({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}EqualTo({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotEqualTo({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}NotEqualTo({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}In(List<{{javaType}}> {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}In({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}In({{javaType}}... {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}In(Arrays.asList({{fieldName}}));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotIn(List<{{javaType}}> {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}NotIn({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotIn({{javaType}}... {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}NotIn(Arrays.asList({{fieldName}}));
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}Between({{javaType}} {{fieldName}}1, {{javaType}} {{fieldName}}2) {
        getCriteria().and{{firstUpFieldName}}Between({{fieldName}}1, {{fieldName}}2);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotBetween({{javaType}} {{fieldName}}1, {{javaType}} {{fieldName}}2) {
        getCriteria().and{{firstUpFieldName}}NotBetween({{fieldName}}1, {{fieldName}}2);
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}GreaterThan({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}GreaterThan({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}GreaterThanOrEqualTo({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}GreaterThanOrEqualTo({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}LessThan({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}LessThan({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}LessThanOrEqualTo({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}LessThanOrEqualTo({{fieldName}});
        return this;
    }

    {{#stringType}}
    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}Like({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}Like({{fieldName}});
        return this;
    }

    public {{metadata.exampleClazzSimpleName}} and{{firstUpFieldName}}NotLike({{javaType}} {{fieldName}}) {
        getCriteria().and{{firstUpFieldName}}NotLike({{fieldName}});
        return this;
    }
    {{/stringType}}
{{/metadata.columnMetadataList}}

    private Criteria getCriteria(){
        if(this.currentCriteria == null){
            this.currentCriteria = new Criteria();
            getOredCriteria().add(this.currentCriteria);
        }
        return this.currentCriteria;
    }

    protected abstract static class GeneratedCriteria implements Serializable {

        private static final long serialVersionUID = 1000001L;

        protected List<Criterion> criteria;

        private boolean valid;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
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
        public Criteria and{{firstUpFieldName}}IsNull() {
            addCriterion("{{columnName}} is null");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}IsNotNull() {
            addCriterion("{{columnName}} is not null");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}EqualTo({{javaType}} {{fieldName}}) {
            {{#encrypt}}
            addCriterion("{{columnName}} =", {{typeHandler}}.encrypt({{fieldName}}), "{{fieldName}}");
            {{/encrypt}}
            {{^encrypt}}
            addCriterion("{{columnName}} =", {{fieldName}}, "{{fieldName}}");
            {{/encrypt}}
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}NotEqualTo({{javaType}} {{fieldName}}) {
            {{#encrypt}}
            addCriterion("{{columnName}} <>", {{typeHandler}}.encrypt({{fieldName}}), "{{fieldName}}");
            {{/encrypt}}
            {{^encrypt}}
            addCriterion("{{columnName}} <>", {{fieldName}}, "{{fieldName}}");
            {{/encrypt}}
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}In(List<{{javaType}}> {{fieldName}}) {
            {{#encrypt}}
            List<String> newList = new ArrayList<>();
            for (String value : {{fieldName}}){
                newList.add({{typeHandler}}.encrypt(value))
            }
            addCriterion("{{columnName}} in", newList, "{{fieldName}}");
            {{/encrypt}}
            {{^encrypt}}
            addCriterion("{{columnName}} in", {{fieldName}}, "{{fieldName}}");
            {{/encrypt}}
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}NotIn(List<{{javaType}}> {{fieldName}}) {
            {{#encrypt}}
            List<String> newList = new ArrayList<>();
            for (String value : {{fieldName}}){
                newList.add({{typeHandler}}.encrypt(value))
            }
            addCriterion("{{columnName}} not in", newList, "{{fieldName}}");
            {{/encrypt}}
            {{^encrypt}}
            addCriterion("{{columnName}} not in", {{fieldName}}, "{{fieldName}}");
            {{/encrypt}}
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}Between({{javaType}} {{fieldName}}1, {{javaType}} {{fieldName}}2) {
            addCriterion("{{columnName}} between", {{fieldName}}1, {{fieldName}}2, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}NotBetween({{javaType}} {{fieldName}}1, {{javaType}} {{fieldName}}2) {
            addCriterion("{{columnName}} not between", {{fieldName}}1, {{fieldName}}2, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}GreaterThan({{javaType}} {{fieldName}}) {
            addCriterion("{{columnName}} >", {{fieldName}}, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}GreaterThanOrEqualTo({{javaType}} {{fieldName}}) {
            addCriterion("{{columnName}} >=", {{fieldName}}, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}LessThan({{javaType}} {{fieldName}}) {
            addCriterion("{{columnName}} <", {{fieldName}}, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}LessThanOrEqualTo({{javaType}} {{fieldName}}) {
            addCriterion("{{columnName}} <=", {{fieldName}}, "{{fieldName}}");
            return (Criteria) this;
        }

        {{#stringType}}
        public Criteria and{{firstUpFieldName}}Like({{javaType}} {{fieldName}}) {
            addCriterion("{{columnName}} like", {{fieldName}}, "{{fieldName}}");
            return (Criteria) this;
        }

        public Criteria and{{firstUpFieldName}}NotLike({{javaType}} {{fieldName}}) {
            addCriterion("{{columnName}} not like", {{fieldName}}, "{{fieldName}}");
            return (Criteria) this;
        }
        {{/stringType}}
{{/metadata.columnMetadataList}}

    }

    public List<String> getColumns() {
        return columns;
    }


    public void setLimit(List<Integer> limit) {
        this.limit = limit;
    }

    public void setOredCriteria(List<Criteria> oredCriteria) {
        this.oredCriteria = oredCriteria;
    }

    public void setCurrentCriteria(Criteria currentCriteria) {
        this.currentCriteria = currentCriteria;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setTable(String table) {
        this.table = table;
    }
    public static class Criteria extends GeneratedCriteria implements Serializable {

        private static final long serialVersionUID = 1000002L;


        protected Criteria() {
            super();
        }
    }

    public static class Criterion implements Serializable {

        private static final long serialVersionUID = 1000003L;

        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private boolean dateValue;

        private String typeHandler;

        public Criterion() {
            super();
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
            if (value instanceof Date) {
                this.dateValue = true;
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
            if (value instanceof Date) {
                this.dateValue = true;
            }
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }


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

        public boolean isDateValue() {
            return dateValue;
        }

        public void setDateValue(boolean dateValue) {
            this.dateValue = dateValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setSecondValue(Object secondValue) {
            this.secondValue = secondValue;
        }

        public void setNoValue(boolean noValue) {
            this.noValue = noValue;
        }

        public void setSingleValue(boolean singleValue) {
            this.singleValue = singleValue;
        }

        public void setBetweenValue(boolean betweenValue) {
            this.betweenValue = betweenValue;
        }

        public void setListValue(boolean listValue) {
            this.listValue = listValue;
        }

        public void setTypeHandler(String typeHandler) {
            this.typeHandler = typeHandler;
        }
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

    protected static List<String> allColumns(){
        List<String> columns = new ArrayList<String>();
        {{#metadata.columnMetadataList}}
        columns.add("{{columnName}}");
        {{/metadata.columnMetadataList}}
        return columns;
    }


    public List<Integer> getLimit(){
        return this.limit;
    }

    public String getGroupByClause(){
        return this.groupByClause;
    }


    static {
        {{#metadata.columnMetadataList}}
        MAPPING.put("{{fieldName}}","{{columnName}}");
        MAPPING.put("{{originColumnName}}","{{columnName}}");
        MAPPING.put("{{columnName}}","{{columnName}}");
        {{/metadata.columnMetadataList}}
    }
}
