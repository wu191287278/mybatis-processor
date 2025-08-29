package {{metadata.packageName}};


import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class {{metadata.repositoryClazzSimpleName}} {

    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private final java.util.concurrent.atomic.AtomicLong counter = new java.util.concurrent.atomic.AtomicLong();

    private final ThreadLocal<Map<String, List<List>>> multiLocal = new ThreadLocal<>();

    private DataSource dataSource;

    private List<String> slaveDataSources  = new ArrayList<>();

    private Map<String, DataSource> dataSourceMap = new HashMap<>();

    private {{metadata.domainClazzSimpleName}}DefaultTypeHandler defaultTypeHandler = new {{metadata.domainClazzSimpleName}}DefaultTypeHandler();

    private String tableName = "{{metadata.tableName}}";

    private final String columnsStr = "{{metadata.columns}}";

    private final List<String> columns = Arrays.asList(columnsStr.split(", "));

    {{#metadata.primaryMetadata}}
    private final String primaryKeyStr = "{{metadata.primaryMetadata.columnName}}";

    private final Class<{{metadata.primaryMetadata.javaType}}> primaryKeyType = {{metadata.primaryMetadata.javaType}}.class;

    private final String primaryKeyCondition = " where " + primaryKeyStr + " = ?";

    private final String primaryKeyInCondition = " where " + primaryKeyStr + " in ";
    {{/metadata.primaryMetadata}}

    private final String selectByPrimaryKeySql = "select " + columnsStr + " from " + tableName + primaryKeyCondition;

    private final String selectByPrimaryKeysSql = "select " + columnsStr + " from " + tableName + primaryKeyInCondition;

    private final String updatePrefix = "update " + tableName;

    private final String updateSuffix = " set " + String.join(" = ?, ",columnsStr.split(",")) + " = ?";

    private final String updateByExamplePrefix = updatePrefix + updateSuffix;

    private final String updateByPrimaryKeySql = updatePrefix + updateSuffix + primaryKeyCondition;

    private final String deletePrefix = "delete from " + tableName;

    private final String deleteByPrimaryKeySql = deletePrefix + primaryKeyCondition;

    private final String deleteByPrimaryKeysSql = deletePrefix + primaryKeyInCondition;

    private final String insertSqlPrefix = "insert into " + tableName + " (" + columnsStr + ") values ";

    private final String insertSql = insertSqlPrefix + appendPlaceholder(columns.size());

    {{#metadata.primaryMetadata}}


    public {{metadata.domainClazzName}} selectByPrimaryKey({{metadata.primaryMetadata.javaType}} {{metadata.primaryMetadata.fieldName}}) {
        return selectOne(selectByPrimaryKeySql, Collections.singletonList({{metadata.primaryMetadata.fieldName}}), this::handle);
    }

    public List<{{metadata.domainClazzName}}> selectByPrimaryKeys(List<{{metadata.primaryMetadata.javaType}}> {{metadata.primaryMetadata.fieldName}}s) {
        String sql = selectByPrimaryKeysSql + appendPlaceholder({{metadata.primaryMetadata.fieldName}}s.size());
        return selectList(sql, {{metadata.primaryMetadata.fieldName}}s, this::handle);
    }

    public long updateByPrimaryKey({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>();
        {{#metadata.columnMetadataList}}
        params.add(getDefaultTypeHandler().get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        {{/metadata.columnMetadataList}}
        return update(updateByPrimaryKeySql, params);
    }

    public long updateByPrimaryKeySelective({{metadata.domainClazzName}} t) {
        StringBuilder prefix = new StringBuilder()
                .append("update ")
                .append(getTableName())
                .append(" set ");
        List<Object> params = new ArrayList<>();
        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            params.add(getDefaultTypeHandler().get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
            prefix.append("{{columnName}} = ?, ");
        }
        {{/metadata.columnMetadataList}}
        params.add(t.get{{metadata.primaryMetadata.firstUpFieldName}}());
        String sql = prefix.substring(0, prefix.length() - 2) + primaryKeyCondition;
        return update(sql, params);
    }

    public long deleteByPrimaryKey({{metadata.primaryMetadata.javaType}} {{metadata.primaryMetadata.fieldName}}) {
        List<Object> params = new ArrayList<>();
        params.add({{metadata.primaryMetadata.fieldName}});
        return delete(deleteByPrimaryKeySql, params);
    }

    public long deleteByPrimaryKeys(List<{{metadata.primaryMetadata.javaType}}> {{metadata.primaryMetadata.fieldName}}s) {
        String sql = deleteByPrimaryKeysSql + appendPlaceholder({{metadata.primaryMetadata.fieldName}}s.size());
        return delete(sql, {{metadata.primaryMetadata.fieldName}}s);
    }

    {{/metadata.primaryMetadata}}

    public {{metadata.domainClazzName}} selectOne({{metadata.exampleClazzName}} example) {
        example.limit(1);
        List<{{metadata.domainClazzName}}> ts = selectByExample(example);
        return ts.isEmpty() ? null : ts.get(0);
    }

    public List<{{metadata.domainClazzName}}> selectByExample({{metadata.exampleClazzName}} example) {
        String sql = toSelectByExampleSql(example);
        List<String> columns = example.getColumns();
        if (columns == null || columns.isEmpty()) {
            return selectList(sql, example.getConditionValues(), this::handle);
        }
        return selectList(sql, example.getConditionValues(), rs -> handle(rs, columns));
    }


    public long countByExample({{metadata.exampleClazzName}} example) {
        String sql = toCountByExampleSql(example);
        return selectOne(sql, example.getConditionValues(), rs -> rs.getLong(1));
    }


    public long updateByExample({{metadata.domainClazzName}} t, {{metadata.exampleClazzName}} example) {
        List<Object> params;
        if (example.getUpdateSetValues() != null) {
            params = new ArrayList<>(example.getUpdateSetValues());
        } else {
            params = new ArrayList<>();
        }

        {{#metadata.columnMetadataList}}
        params.add(defaultTypeHandler.get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        {{/metadata.columnMetadataList}}
        String condition = toConditionSql(example);
        String sql;
        if (example.getUpdateExpression() != null) {
            sql = updateByExamplePrefix + String.join(", ", example.getUpdateExpression()) + condition;
        } else {
            sql = updateByExamplePrefix + condition;
        }
        params.addAll(example.getConditionValues());
        return update(sql, params);
    }

    public long updateByExampleSelective({{metadata.domainClazzName}} t, {{metadata.exampleClazzName}} example) {
        StringBuilder prefix = new StringBuilder()
                .append("update ")
                .append(getTableName())
                .append(" set ");

        List<Object> params;
        if (example.getUpdateSetValues() != null) {
            params = new ArrayList<>(example.getUpdateSetValues());
        } else {
            params = new ArrayList<>();
        }

        if (example.getUpdateExpression() != null) {
            prefix.append(String.join(", ", example.getUpdateExpression()));
        }

        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            prefix.append("{{columnName}} = ?, ");
            params.add(getDefaultTypeHandler().get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        }
        {{/metadata.columnMetadataList}}
        params.addAll(example.getConditionValues());
        String sql = prefix.substring(0, prefix.length() - 2) + toConditionSql(example);
        return update(sql, params);
    }

    public long updateByExampleSelective({{metadata.exampleClazzName}} example) {
        StringBuilder prefix = new StringBuilder()
                .append("update ")
                .append(getTableName())
                .append(" set ");

        List<Object> params;
        if (example.getUpdateSetValues() != null) {
            params = new ArrayList<>(example.getUpdateSetValues());
        } else {
            params = new ArrayList<>();
        }

        if (example.getUpdateExpression() != null) {
            prefix.append(String.join(", ", example.getUpdateExpression()));
        }

        params.addAll(example.getConditionValues());
        String sql = prefix + toConditionSql(example);
        return update(sql, params);
    }

    public void insert({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>(columns.size());
        {{#metadata.columnMetadataList}}
        params.add(getDefaultTypeHandler().get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        {{/metadata.columnMetadataList}}
        {{#metadata.primaryMetadata}}
        {{metadata.primaryMetadata.javaType}} primaryKey = insert(insertSql, params);
        if (primaryKey > 0) {
            t.set{{metadata.primaryMetadata.firstUpFieldName}}(primaryKey);
        }
        {{/metadata.primaryMetadata}}

    }

    public void insertBatch(List<{{metadata.domainClazzName}}> ts) {
        if (ts == null || ts.isEmpty()) {
            throw new IllegalArgumentException("ts is null or empty");
        }
        List<Object> params = new ArrayList<>(columns.size() * ts.size());
        StringBuilder sql = new StringBuilder(insertSqlPrefix);

        for ({{metadata.domainClazzName}} t : ts) {
            {{#metadata.columnMetadataList}}
            params.add(getDefaultTypeHandler().get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
            {{/metadata.columnMetadataList}}
            sql.append(appendPlaceholder(columns.size()))
                    .append(", ");
        }
        {{#metadata.primaryMetadata}}
        List<{{metadata.primaryMetadata.javaType}}> primaryKeys = insertBatch(sql.substring(0, sql.length() - 2), params);
        for (int i = 0; i < primaryKeys.size(); i++) {
            {{metadata.primaryMetadata.javaType}} primaryKey = primaryKeys.get(i);
            if (primaryKey > 0) {
                ts.get(i).set{{metadata.primaryMetadata.firstUpFieldName}}(primaryKey);
            }
        }
        {{/metadata.primaryMetadata}}
    }

    public void insertSelective({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>();

        StringBuilder prefix = new StringBuilder()
                .append("insert into ")
                .append(getTableName())
                .append(" (");

        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            prefix.append("{{columnName}}, ");
            params.add(getDefaultTypeHandler().get{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        }
        {{/metadata.columnMetadataList}}


        String sql = prefix.substring(0, prefix.length() - 2) + ") values " + appendPlaceholder(params.size());
        {{#metadata.primaryMetadata}}
        {{metadata.primaryMetadata.javaType}} primaryKey = insert(sql, params);
        if (primaryKey > 0) {
            t.set{{metadata.primaryMetadata.firstUpFieldName}}(primaryKey);
        }
        {{/metadata.primaryMetadata}}
    }


    public long deleteByExample({{metadata.exampleClazzName}} example) {
        String sql = deletePrefix + toConditionSql(example);
        return delete(sql, example.getConditionValues());
    }

    protected <T> List<T> selectList(String sql, List params, Handler<T> handler) {
        List<T> list = new ArrayList<>();
        Connection connection = getConnection(true);
        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    T result = handler.handle(resultSet);
                    list.add(result);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
        if (log.isDebugEnabled()) {
            log.debug("Total:      {}", list.size());
        }
        return list;
    }


    public void consumeByExample({{metadata.exampleClazzName}} example, Consumer<{{metadata.domainClazzName}}> consume) {
        String sql = toSelectByExampleSql(example);
        List<String> columns = example.getColumns();
        if (columns == null || columns.isEmpty()) {
            consume(sql, example.getConditionValues(), this::handle,consume);
            return;
        }
        consume(sql, example.getConditionValues(), rs -> handle(rs, columns),consume);
    }


    protected <T> void consume(String sql, List params, Handler<T> handler, Consumer<T> consumer) {
        List<T> list = new ArrayList<>();
        Connection connection = getConnection(true);
        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        boolean autoCommit;
        try {
            autoCommit = connection.getAutoCommit();
            if (!autoCommit) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            statement.setFetchSize(Integer.MIN_VALUE);
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    T handle = handler.handle(resultSet);
                    consumer.accept(handle);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (autoCommit) {
                close(connection);
            }
        }
    }


    protected <T> T selectOne(String sql, List<Object> params, Handler<T> handler) {
        Connection connection = getConnection(true);
        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Total:      {}", 1);
                    }
                    return handler.handle(resultSet);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Total:      {}", 0);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
        return null;
    }

    protected long update(String sql, List params) {
        Map<String, List<List>> valueMap = multiLocal.get();
        if (valueMap != null) {
            valueMap.computeIfAbsent(sql, k -> new ArrayList<>()).add(params);
            return 0;
        }
        Connection connection = getConnection();

        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            int affect = statement.executeUpdate();
            if (log.isDebugEnabled()) {
                log.debug("Total:      {}", affect);
            }
            return affect;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected long delete(String sql, List params) {
        Map<String, List<List>> valueMap = multiLocal.get();
        if (valueMap != null) {
            valueMap.computeIfAbsent(sql, k -> new ArrayList<>()).add(params);
            return 0;
        }
        Connection connection = getConnection();
        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            int affect = statement.executeUpdate();
            if (log.isDebugEnabled()) {
                log.debug("Total:      {}", affect);
            }
            return affect;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected {{metadata.primaryMetadata.javaType}} insert(String sql, List params) {
        Map<String, List<List>> valueMap = multiLocal.get();
        if (valueMap != null) {
            valueMap.computeIfAbsent(sql, k -> new ArrayList<>()).add(params);
            return {{metadata.primaryMetadata.javaType}}.valueOf(0);
        }
        Connection connection = getConnection();
        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, params);
            int affect = statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getObject(1, {{metadata.primaryMetadata.javaType}}.class);
            }

            if (log.isDebugEnabled()) {
                log.debug("Total:      {}", affect);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
        return {{metadata.primaryMetadata.javaType}}.valueOf(0);
    }


    protected List<{{metadata.primaryMetadata.javaType}}> insertBatch(String sql, List<Object> params) {
        Connection connection = getConnection();
        if (log.isDebugEnabled()) {
            log.debug("Preparing:  {}", sql);
            log.debug("Parameters: {}", params);
        }
        List<{{metadata.primaryMetadata.javaType}}> ids = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, params);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            while (generatedKeys.next()) {
                {{metadata.primaryMetadata.javaType}} primaryKey = generatedKeys.getObject(1, {{metadata.primaryMetadata.javaType}}.class);
                ids.add(primaryKey);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
        if (log.isDebugEnabled()) {
            log.debug("Total:      {}", ids.size());
        }
        return ids;
    }


    protected void closeCheckTx(Connection connection) {
        try {
            if (connection != null && !connection.isClosed() && connection.getAutoCommit()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    protected void setParameters(PreparedStatement ps, List params) throws SQLException {
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                setParameter(ps, i + 1, param);
            }
        }
    }

    protected void setParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {
        if (param == null) {
            ps.setNull(paramIndex, JDBCType.NULL.getVendorTypeNumber());
            return;
        } else if (param instanceof java.util.Date) {
            if (param instanceof Date) {
                ps.setDate(paramIndex, (Date) param);
            } else if (param instanceof Time) {
                ps.setTime(paramIndex, (Time) param);
            } else {
                ps.setTimestamp(paramIndex, new Timestamp(((java.util.Date) param).getTime()));
            }
            return;
        }
        if (param instanceof Enum) {
            ps.setString(paramIndex, String.valueOf(param));
            return;
        } else if (param instanceof Number) {
            if (param instanceof BigDecimal) {
                ps.setBigDecimal(paramIndex, (BigDecimal) param);
                return;
            }
            if (param instanceof BigInteger) {
                ps.setBigDecimal(paramIndex, new BigDecimal((BigInteger) param));
                return;
            }
        } else if (param instanceof Enum) {
            ps.setObject(paramIndex, param.toString());
            return;
        }
        ps.setObject(paramIndex, param);
    }


    protected interface Handler<R> {
        R handle(ResultSet rs) throws SQLException;

    }


    protected String appendPlaceholder(int size) {
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


    protected String toConditionSql({{metadata.exampleClazzName}} example) {
        List<List<String>> orConditions = example.getOrConditions();
        List<String> conditions = example.getConditions();
        String orderByClause = example.getOrderByClause();
        List<Integer> limit = example.getLimit();
        StringBuilder sql = new StringBuilder();
        if (orConditions != null && !orConditions.isEmpty()) {
            sql.append(" where ");

            for (List<String> orCondition : orConditions) {
                sql.append("(");
                sql.append(String.join(" and ", orCondition));
                sql.append(")");
                sql.append(" or ");
            }

            sql.append("(");
            sql.append(String.join(" and ", conditions));
            sql.append(")");

        } else if (!conditions.isEmpty()) {
            sql.append(" where ");
            sql.append(String.join(" and ", conditions));
        }

        if (orderByClause != null && !orderByClause.trim().isEmpty()) {
            sql.append(" order by ");
            sql.append(orderByClause);
        }

        if (limit != null && !limit.isEmpty()) {
            sql.append(" limit ");
            if (limit.size() == 1) {
                sql.append(limit.get(0));
            }
            if (limit.size() == 2) {
                sql.append(limit.get(0));
                sql.append(", ");
                sql.append(limit.get(1));
            }
        }
        return sql.toString();
    }


    protected {{metadata.domainClazzName}} handle(ResultSet rs, List<String> columns) throws SQLException {
        {{metadata.domainClazzName}} t = new {{metadata.domainClazzName}}();
        for (String column : columns) {
            {{#metadata.columnMetadataList}}
            if ("{{originColumnName}}".equals(column) || "{{columnName}}".equals(column) || "{{fieldName}}".equals(column)) {
                defaultTypeHandler.set{{firstUpFieldName}}(rs, t, "{{originColumnName}}", {{javaType}}.class);
            }
            {{/metadata.columnMetadataList}}
        }
        return t;
    }


    protected {{metadata.domainClazzName}} handle(ResultSet rs) throws SQLException {
        {{metadata.domainClazzName}} t = new {{metadata.domainClazzName}}();
        {{#metadata.columnMetadataList}}
        defaultTypeHandler.set{{firstUpFieldName}}(rs, t, "{{originColumnName}}", {{javaType}}.class);
        {{/metadata.columnMetadataList}}
        return t;
    }


    protected String toSelectByExampleSql({{metadata.exampleClazzName}} example) {
        boolean distinct = example.isDistinct();
        List<String> selectColumns = example.getColumns();
        String table = example.getTable() == null ? getTableName() : example.getTable();
        StringBuilder sql = new StringBuilder();

        sql.append("select ");
        if (distinct) {
            sql.append("distinct ");
        }

        if (selectColumns != null && selectColumns.isEmpty()) {
            sql.append(String.join(", ", selectColumns));
        } else {
            sql.append(columnsStr);
        }
        sql.append(" from ");
        sql.append(table);
        sql.append(toConditionSql(example));
        return sql.toString();
    }

    protected String toCountByExampleSql({{metadata.exampleClazzName}} example) {
        boolean distinct = example.isDistinct();
        List<String> selectColumns = example.getColumns();
        String table = example.getTable() == null ? getTableName() : example.getTable();
        StringBuilder sql = new StringBuilder();

        sql.append("select ");
        if (selectColumns != null && !selectColumns.isEmpty()) {
            sql.append("count(");
            if (distinct) {
                sql.append("distinct ");
            }
            sql.append(String.join(", ", selectColumns));
            sql.append(")");
        } else {
            sql.append("count(*)");
        }
        sql.append(" from ");
        sql.append(table);
        sql.append(toConditionSql(example));
        return sql.toString();
    }

    protected String getTableName() {
        return tableName;
    }

    protected synchronized void setTableName(String tableName) {
        this.tableName = tableName;
    }

    protected Connection getConnection() {
        return getConnection(false);
    }

    {{#metadata.useSpring}}
    protected Connection getConnection(boolean isSelect) {
        if(isSelect && !slaveDataSources.isEmpty() && !isActualTransactionActive()){
            try {
               String name = slaveDataSources.get((int) (counter.incrementAndGet() % slaveDataSources.size()));
                if(log.isDebugEnabled()){
                   log.debug("Use slave dataSource {}",name);
               }
               Connection connection =  dataSourceMap.get(name).getConnection();
               connection.setReadOnly(true);
               return connection;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return org.springframework.jdbc.datasource.DataSourceUtils.getConnection(this.getDataSource());
    }
    {{/metadata.useSpring}}

    {{^metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired
    protected Connection getConnection(boolean isSelect) {
        try {
            return this.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    {{/metadata.useSpring}}
    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired{{/metadata.useSpring}}
    {{#metadata.dataSource}}@org.springframework.beans.factory.annotation.Qualifier("{{metadata.dataSource}}"){{/metadata.dataSource}}
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired{{/metadata.useSpring}}
    public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
        {{#metadata.slaveDataSources}}
        if(dataSourceMap.get("{{.}}") != null){
            slaveDataSources.add("{{.}}");
        }
        {{/metadata.slaveDataSources}}
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void multiStart() {
        Map<String, List<List>> valueMap = multiLocal.get();
        if (valueMap == null) {
            multiLocal.set(new java.util.LinkedHashMap<>());
        }
    }

    public void multiRemove() {
        multiLocal.remove();
    }

    public long multiEnd() {
        Map<String, List<List>> valueMap = multiLocal.get();
        if (valueMap == null || valueMap.isEmpty()) {
            return 0;
        }
        multiLocal.remove();

        long count = 0;
        Connection connection = getConnection();
        boolean isAutoCommit = true;
        try {
            isAutoCommit = connection.getAutoCommit();
            if (isAutoCommit) {
                connection.setAutoCommit(false);
            }
            for (Map.Entry<String, List<List>> entry : valueMap.entrySet()) {
                String sql = entry.getKey();
                List<List> values = entry.getValue();
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    for (List params : values) {
                        if (log.isDebugEnabled()) {
                            log.debug("Preparing:  {}", sql);
                            log.debug("Parameters: {}", params);
                        }
                        setParameters(statement, params);
                        statement.addBatch();
                    }
                    int[] ints = statement.executeBatch();
                    for (int c : ints) {
                        count += c;
                        if (log.isDebugEnabled()) {
                            log.debug("Total:      {}", c);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (isAutoCommit) {
                try {
                    connection.commit();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    close(connection);
                }
            } else {
                closeCheckTx(connection);
            }
        }
        return count;
    }

    {{#metadata.useSpring}}
    protected boolean isActualTransactionActive(){
        return org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive();
    }
    {{/metadata.useSpring}}
    {{^metadata.useSpring}}
    protected boolean isActualTransactionActive(){
        return false;
    }
    {{/metadata.useSpring}}


    public static class {{metadata.domainClazzSimpleName}}DefaultTypeHandler {

        {{#metadata.columnMetadataList}}
        public void set{{firstUpFieldName}}(ResultSet resultSet, {{metadata.domainClazzSimpleName}} t, String name, Class<{{javaType}}> type) throws SQLException {
            {{#isEnums}}
            String value = resultSet.getString(name);
            if(value == null){
                return;
            }
            t.set{{firstUpFieldName}}(Enum.valueOf(type, value));
            {{/isEnums}}
            {{^isEnums}}
            {{javaType}} value = resultSet.getObject(name, type);
            if(value == null){
                return;
            }
            t.set{{firstUpFieldName}}(value);
            {{/isEnums}}
        }

        public Object get{{firstUpFieldName}}({{javaType}} value) {
            {{^isEnums}}return value;{{/isEnums}}{{#isEnums}}return value == null ? null: String.valueOf(value);{{/isEnums}}
        }

        public List get{{firstUpFieldName}}List(List<{{javaType}}> values) {
            {{^isEnums}}return values;{{/isEnums}}
            {{#isEnums}}return values.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList());{{/isEnums}}
        }

        {{/metadata.columnMetadataList}}
    }

    public {{metadata.domainClazzSimpleName}}DefaultTypeHandler getDefaultTypeHandler() {
        return defaultTypeHandler;
    }

}
