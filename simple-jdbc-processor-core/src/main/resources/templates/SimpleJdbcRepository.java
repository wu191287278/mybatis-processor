package {{metadata.packageName}};


import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class {{metadata.repositoryClazzSimpleName}} {

    protected final org.slf4j.Logger log;

    private final java.util.concurrent.atomic.AtomicLong counter = new java.util.concurrent.atomic.AtomicLong();

    private DataSource dataSource;

    private List<String> slaveDataSources  = new ArrayList<>();

    private Map<String, DataSource> dataSourceMap = new HashMap<>();

    private {{metadata.typeHandlerClazzName}} defaultTypeHandler = new {{metadata.typeHandlerClazzName}}();

    private String tableName = "{{metadata.tableName}}";

    private String columnsStr = "{{metadata.columns}}";

    private List<String> columns = Arrays.asList(columnsStr.split(", "));

    private String primaryKeyStr = "{{metadata.primaryMetadata.columnName}}";

    private Class<{{metadata.primaryMetadata.javaType}}> primaryKeyType = {{metadata.primaryMetadata.javaType}}.class;

    private String primaryKeyCondition;

    private String primaryKeyInCondition;

    private String selectByPrimaryKeySql;

    private String selectByPrimaryKeyForUpdateSql;

    private String selectByPrimaryKeysSql;

    private String updatePrefix;

    private String updateSuffix;

    private String updateByExamplePrefix;

    private String updateByPrimaryKeySql;

    private String deletePrefix;

    private String deleteByPrimaryKeySql;

    private String deleteByPrimaryKeysSql;

    private String insertSqlPrefix;

    private String insertSql;


    public {{metadata.repositoryClazzSimpleName}}(){
        this.log = org.slf4j.LoggerFactory.getLogger(this.getClass());
        init();
    }
{{#metadata.shard}}
    public {{metadata.repositoryClazzSimpleName}}(Class<?> logClazz, String tableName, DataSource dataSource, Map<String, DataSource> dataSourceMap, {{metadata.typeHandlerClazzName}} defaultTypeHandler){
        this.log = org.slf4j.LoggerFactory.getLogger(logClazz);
        this.tableName = tableName;
        this.dataSource = dataSource;
        this.defaultTypeHandler = defaultTypeHandler;
        this.dataSourceMap = dataSourceMap;
        this.slaveDataSources = new ArrayList<>(dataSourceMap.keySet());
        init();
    }
{{/metadata.shard}}

    private void init() {
        this.primaryKeyCondition = " where " + primaryKeyStr + " = ?";
        this.primaryKeyInCondition = " where " + primaryKeyStr + " in ";
        this.selectByPrimaryKeySql = "select " + columnsStr + " from " + tableName + primaryKeyCondition;
        this.selectByPrimaryKeyForUpdateSql = "select " + columnsStr + " from " + tableName + primaryKeyCondition + " for update";
        this.selectByPrimaryKeysSql = "select " + columnsStr + " from " + tableName + primaryKeyInCondition;
        this.updatePrefix = "update " + tableName;
        this.updateSuffix = " set " + String.join(" = ?, ", columnsStr.split(",")) + " = ?";
        this.updateByExamplePrefix = updatePrefix + updateSuffix;
        this.updateByPrimaryKeySql = updatePrefix + updateSuffix + primaryKeyCondition;
        this.deletePrefix = "delete from " + tableName;
        this.deleteByPrimaryKeySql = deletePrefix + primaryKeyCondition;
        this.deleteByPrimaryKeysSql = deletePrefix + primaryKeyInCondition;
        this.insertSqlPrefix = "insert into " + tableName + " (" + columnsStr + ") values ";
        this.insertSql = insertSqlPrefix + appendPlaceholder(columns.size());
    }

    {{#metadata.primaryMetadata}}


    public {{metadata.domainClazzName}} selectByPrimaryKey({{metadata.primaryMetadata.javaType}} {{metadata.primaryMetadata.fieldName}}) {
        return selectOne(selectByPrimaryKeySql, Collections.singletonList({{metadata.primaryMetadata.fieldName}}), this::handle);
    }

    public {{metadata.domainClazzName}} selectByPrimaryKeyForUpdate({{metadata.primaryMetadata.javaType}} {{metadata.primaryMetadata.fieldName}}) {
        return selectOne(selectByPrimaryKeyForUpdateSql, Collections.singletonList({{metadata.primaryMetadata.fieldName}}), this::handle);
    }

    public List<{{metadata.domainClazzName}}> selectByPrimaryKeys(List<{{metadata.primaryMetadata.javaType}}> {{metadata.primaryMetadata.fieldName}}s) {
        String sql = selectByPrimaryKeysSql + appendPlaceholder({{metadata.primaryMetadata.fieldName}}s.size());
        return selectList(sql, {{metadata.primaryMetadata.fieldName}}s, this::handle);
    }

    public long updateByPrimaryKey({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>(columns.size());
        {{#metadata.columnMetadataList}}
        params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        {{/metadata.columnMetadataList}}
        return update(updateByPrimaryKeySql, params);
    }

    public long updateByPrimaryKeySelective({{metadata.domainClazzName}} t) {
        StringBuilder prefix = new StringBuilder()
                .append("update ")
                .append(getTableName())
                .append(" set ");
        List<Object> params = new ArrayList<>(columns.size());
        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
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
            return selectList(sql, getConditionValues(example), this::handle);
        }
        return selectList(sql, getConditionValues(example), rs -> handle(rs, columns));
    }


    public long countByExample({{metadata.exampleClazzName}} example) {
        String sql = toCountByExampleSql(example);
        return selectOne(sql, getConditionValues(example), rs -> rs.getLong(1));
    }


    public long updateByExample({{metadata.domainClazzName}} t, {{metadata.exampleClazzName}} example) {
        List<Object> params;
        if (example.getUpdateSetValues() != null) {
            params = new ArrayList<>(example.getUpdateSetValues());
        } else {
            params = new ArrayList<>(columns.size());
        }

        {{#metadata.columnMetadataList}}
        params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        {{/metadata.columnMetadataList}}
        String condition = toConditionSql(example);
        String sql;
        if (example.getUpdateExpression() != null) {
            sql = updateByExamplePrefix + String.join(", ", example.getUpdateExpression()) + condition;
        } else {
            sql = updateByExamplePrefix + condition;
        }
        params.addAll(getConditionValues(example));
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
            params = new ArrayList<>(columns.size());
        }

        if (example.getUpdateExpression() != null) {
            prefix.append(String.join(", ", example.getUpdateExpression()));
        }

        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            prefix.append("{{columnName}} = ?, ");
            params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        }
        {{/metadata.columnMetadataList}}
        params.addAll(getConditionValues(example));
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
            params = new ArrayList<>(columns.size());
        }

        if (example.getUpdateExpression() != null) {
            prefix.append(String.join(", ", example.getUpdateExpression()));
        }

        params.addAll(getConditionValues(example));
        String sql = prefix + toConditionSql(example);
        return update(sql, params);
    }

    public void insert({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>(columns.size());
        {{#metadata.columnMetadataList}}
        params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
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
            params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
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
        List<Object> params = new ArrayList<>(columns.size());

        StringBuilder prefix = new StringBuilder()
                .append("insert into ")
                .append(getTableName())
                .append(" (");

        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            prefix.append("{{columnName}}, ");
            params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
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


    public void replaceSelective({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>(columns.size());

        StringBuilder prefix = new StringBuilder()
                .append("replace into ")
                .append(getTableName())
                .append(" (");

        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            prefix.append("{{columnName}}, ");
            params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
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


    public {{metadata.primaryMetadata.javaType}} insertIgnoreSelective({{metadata.domainClazzName}} t) {
        List<Object> params = new ArrayList<>(columns.size());

        StringBuilder prefix = new StringBuilder()
                .append("insert ignore into ")
                .append(getTableName())
                .append(" (");

        {{#metadata.columnMetadataList}}
        if (t.get{{firstUpFieldName}}() != null) {
            prefix.append("{{columnName}}, ");
            params.add(getDefaultTypeHandler().encode{{firstUpFieldName}}(t.get{{firstUpFieldName}}()));
        }
        {{/metadata.columnMetadataList}}


        String sql = prefix.substring(0, prefix.length() - 2) + ") values " + appendPlaceholder(params.size());
        {{#metadata.primaryMetadata}}
        {{metadata.primaryMetadata.javaType}} primaryKey = insert(sql, params);
        if (primaryKey > 0) {
            t.set{{metadata.primaryMetadata.firstUpFieldName}}(primaryKey);
        }
        {{/metadata.primaryMetadata}}
        return primaryKey;
    }


    public long deleteByExample({{metadata.exampleClazzName}} example) {
        String sql = deletePrefix + toConditionSql(example);
        return delete(sql, getConditionValues(example));
    }

    protected <T> List<T> selectList(String sql, List params, Handler<T> handler) {
        List<T> list = new ArrayList<>();
        Connection connection = getConnection(true);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
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
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Total:      {}", list.size());
        }
        return list;
    }


    public void consumeByExample({{metadata.exampleClazzName}} example, Consumer<{{metadata.domainClazzName}}> consume) {
        String sql = toSelectByExampleSql(example);
        List<String> columns = example.getColumns();
        if (columns == null || columns.isEmpty()) {
            consume(sql, getConditionValues(example), this::handle,consume);
            return;
        }
        consume(sql, getConditionValues(example), rs -> handle(rs, columns),consume);
    }


    protected <T> void consume(String sql, List params, Handler<T> handler, Consumer<T> consumer) {
        List<T> list = new ArrayList<>();
        Connection connection = getConnection(true);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
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


    protected <T> T selectOne(String sql, List params, Handler<T> handler) {
        Connection connection = getConnection(true);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Total:      {}", 1);
                    }
                    return handler.handle(resultSet);
                } else {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Total:      {}", 0);
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
        Connection connection = getConnection();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            int affect = statement.executeUpdate();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Total:      {}", affect);
            }
            return affect;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
    }

    protected int[] updateBatch(String sql, List<Object[]> batchParams) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Object[] batchParam : batchParams) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Preparing:  {}", sql);
                    getLogger().debug("Parameters: {}", Arrays.toString(batchParam));
                }
                setParameters(statement, batchParam);
                statement.addBatch();
            }
            int[] affects = statement.executeBatch();
            if (getLogger().isDebugEnabled()) {
                for (int affect : affects) {
                    getLogger().debug("Total:      {}", affect);
                }
            }
            return affects;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
    }

    protected long delete(String sql, List params) {
        Connection connection = getConnection();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            int affect = statement.executeUpdate();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Total:      {}", affect);
            }
            return affect;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
    }

    protected {{metadata.primaryMetadata.javaType}} insert(String sql, List params) {
        Connection connection = getConnection();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql{{#metadata.primaryMetadata.useGeneratedKeys}}, Statement.RETURN_GENERATED_KEYS{{/metadata.primaryMetadata.useGeneratedKeys}})) {
            setParameters(statement, params);
            int affect = statement.executeUpdate();
            {{#metadata.primaryMetadata.useGeneratedKeys}}
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getObject(1, {{metadata.primaryMetadata.javaType}}.class);
            }
            {{/metadata.primaryMetadata.useGeneratedKeys}}
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Total:      {}", affect);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
        return {{metadata.primaryMetadata.defaultValue}};
    }


    protected List<{{metadata.primaryMetadata.javaType}}> insertBatch(String sql, List<Object> params) {
        Connection connection = getConnection();
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Preparing:  {}", sql);
            getLogger().debug("Parameters: {}", params);
        }
        List<{{metadata.primaryMetadata.javaType}}> ids = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql{{#metadata.primaryMetadata.useGeneratedKeys}}, Statement.RETURN_GENERATED_KEYS{{/metadata.primaryMetadata.useGeneratedKeys}})) {
            setParameters(statement, params);
            statement.executeUpdate();
            {{#metadata.primaryMetadata.useGeneratedKeys}}
            ResultSet generatedKeys = statement.getGeneratedKeys();
            while (generatedKeys.next()) {
                {{metadata.primaryMetadata.javaType}} primaryKey = generatedKeys.getObject(1, {{metadata.primaryMetadata.javaType}}.class);
                ids.add(primaryKey);
            }
            {{/metadata.primaryMetadata.useGeneratedKeys}}
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeCheckTx(connection);
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Total:      {}", ids.size());
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

    protected void setParameters(PreparedStatement ps, Object[] params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                setParameter(ps, i + 1, params[i]);
            }
        }
    }

    protected void setParameter(PreparedStatement ps, int paramIndex, Object param) throws SQLException {
        if (param == null) {
            ps.setNull(paramIndex, JDBCType.NULL.getVendorTypeNumber());
            return;
        }

        if (param instanceof Boolean) {
            ps.setBoolean(paramIndex, (Boolean) param);
            return;
        }

        if (param instanceof CharSequence) {
            String str = param.toString();
            if (str.length() > 40000) {
                ps.setClob(paramIndex, new java.io.StringReader(str));
            } else {
                ps.setString(paramIndex, str);
            }
            return;
        }

        if (param instanceof byte[]) {
            ps.setBytes(paramIndex, (byte[]) param);
            return;
        }

        if (param instanceof Enum) {
            ps.setString(paramIndex, ((Enum<?>) param).name());
            return;
        }

        if (param instanceof java.util.Date) {
            if (param instanceof java.sql.Date) {
                ps.setDate(paramIndex, (java.sql.Date) param);
            } else if (param instanceof java.sql.Time) {
                ps.setTime(paramIndex, (java.sql.Time) param);
            } else {
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) param).getTime()));
            }
            return;
        }

        if (param instanceof java.time.LocalDate) {
            ps.setDate(paramIndex, java.sql.Date.valueOf((java.time.LocalDate) param));
            return;
        } else if (param instanceof java.time.LocalTime) {
            ps.setTime(paramIndex, java.sql.Time.valueOf((java.time.LocalTime) param));
            return;
        } else if (param instanceof java.time.LocalDateTime) {
            ps.setTimestamp(paramIndex, java.sql.Timestamp.valueOf((java.time.LocalDateTime) param));
            return;
        } else if (param instanceof java.time.ZonedDateTime) {
            ps.setTimestamp(paramIndex, java.sql.Timestamp.from(((java.time.ZonedDateTime) param).toInstant()));
            return;
        } else if (param instanceof java.util.Calendar){
            ps.setTimestamp(paramIndex, java.sql.Timestamp.from(((java.util.Calendar) param).toInstant()));
        }

        if (param instanceof UUID) {
            ps.setString(paramIndex, ((UUID) param).toString());
            return;
        }

        if (param instanceof Number) {
            if (param instanceof Byte) {
                ps.setByte(paramIndex, (Byte) param);
            } else if (param instanceof Short) {
                ps.setShort(paramIndex, (Short) param);
            } else if (param instanceof Integer) {
                ps.setInt(paramIndex, (Integer) param);
            } else if (param instanceof Long) {
                ps.setLong(paramIndex, (Long) param);
            } else if (param instanceof Float) {
                ps.setFloat(paramIndex, (Float) param);
            } else if (param instanceof Double) {
                ps.setDouble(paramIndex, (Double) param);
            } else if (param instanceof BigDecimal) {
                ps.setBigDecimal(paramIndex, (BigDecimal) param);
            } else if (param instanceof BigInteger) {
                ps.setBigDecimal(paramIndex, new BigDecimal((BigInteger) param));
            }
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

    protected List getConditionValues({{metadata.exampleClazzName}} example) {
        List<Object> params = new ArrayList<>();
        List<List<{{metadata.exampleClazzSimpleName}}.Criteria>> orConditions = example.getOrConditions();
        if (orConditions != null && !orConditions.isEmpty()) {
            for (List<{{metadata.exampleClazzSimpleName}}.Criteria> orCondition : orConditions) {
                for ({{metadata.exampleClazzSimpleName}}.Criteria criteria : orCondition) {
                    convertConditionParam(params, criteria);
                }
            }
        } else {
            for ({{metadata.exampleClazzSimpleName}}.Criteria criteria : example.getCriteries()) {
                convertConditionParam(params, criteria);
            }
        }
        return params;
    }

    private void convertConditionParam(List params, {{metadata.exampleClazzSimpleName}}.Criteria criteria){
        String column = criteria.getColumn();
        {{#metadata.columnMetadataList}}
        if ("{{originColumnName}}".equals(column) || "{{columnName}}".equals(column) || "{{fieldName}}".equals(column)) {
            if (criteria.getValue() != null) {
                Object value = getDefaultTypeHandler().encode{{firstUpFieldName}}(({{javaType}}) criteria.getValue());
                params.add(value);
            }
            if (criteria.getSecondValue() != null) {
                Object value = getDefaultTypeHandler().encode{{firstUpFieldName}}(({{javaType}}) criteria.getSecondValue());
                params.add(value);
            }
            if (criteria.getListValue() != null) {
                List values = getDefaultTypeHandler().encode{{firstUpFieldName}}List((List<{{javaType}}>) criteria.getListValue());
                params.addAll(values);
            }
        }
        {{/metadata.columnMetadataList}}

    }

    protected String toConditionSql({{metadata.exampleClazzName}} example) {
        List<List<{{metadata.exampleClazzName}}.Criteria>> orConditions = example.getOrConditions();

        String orderByClause = example.getOrderByClause();
        List<Integer> limit = example.getLimit();
        StringBuilder sql = new StringBuilder();
        if (orConditions != null && !orConditions.isEmpty()) {
            sql.append(" where ");
            orConditions.add(example.getCriteries());
            for (int j = 0; j < orConditions.size(); j++) {
                if (j > 0 && j < orConditions.size() ) {
                    sql.append(" or ");
                }
                List<{{metadata.exampleClazzName}}.Criteria> criteris = orConditions.get(j);
                sql.append("(");
                for (int i = 0; i < criteris.size(); i++) {
                    if (i > 0 && i < criteris.size() ) {
                        sql.append(" and ");
                    }
                    {{metadata.exampleClazzName}}.Criteria criteria = criteris.get(i);
                    if (criteria.getValue() != null && criteria.getSecondValue() != null) {
                        sql.append(criteria.getColumn())
                                .append(criteria.getCondition())
                                .append("? and ?");
                    } else if (criteria.getValue() != null) {
                        sql.append(criteria.getColumn())
                                .append(criteria.getCondition())
                                .append("?");
                    } else if (criteria.getListValue() != null) {
                        sql.append(criteria.getColumn())
                                .append(criteria.getCondition())
                                .append(appendPlaceholder(criteria.getListValue().size()));
                    }
                }
                sql.append(")");
            }

        } else if (!example.getCriteries().isEmpty()) {
            sql.append(" where ");
            List<{{metadata.exampleClazzName}}.Criteria> criteris = example.getCriteries();
            for (int i = 0; i < criteris.size(); i++) {
                if (i > 0 && i < criteris.size() ) {
                    sql.append(" and ");
                }
                {{metadata.exampleClazzName}}.Criteria criteria = criteris.get(i);
                if (criteria.getValue() != null && criteria.getSecondValue() != null) {
                    sql.append(criteria.getColumn())
                            .append(criteria.getCondition())
                            .append("? and ?");
                } else if (criteria.getValue() != null) {
                    sql.append(criteria.getColumn())
                            .append(criteria.getCondition())
                            .append("?");
                } else if (criteria.getListValue() != null) {
                    sql.append(criteria.getColumn())
                            .append(criteria.getCondition())
                            .append(appendPlaceholder(criteria.getListValue().size()));
                }
            }
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
                getDefaultTypeHandler().decode{{firstUpFieldName}}(rs, t, "{{originColumnName}}", {{javaType}}.class);
            }
            {{/metadata.columnMetadataList}}
        }
        return t;
    }


    protected {{metadata.domainClazzName}} handle(ResultSet rs) throws SQLException {
        {{metadata.domainClazzName}} t = new {{metadata.domainClazzName}}();
        {{#metadata.columnMetadataList}}
        getDefaultTypeHandler().decode{{firstUpFieldName}}(rs, t, "{{originColumnName}}", {{javaType}}.class);
        {{/metadata.columnMetadataList}}
        return t;
    }


    protected String toSelectByExampleSql({{metadata.exampleClazzName}} example) {
        boolean distinct = example.isDistinct();
        List<String> selectColumns = example.getColumns();
        String table = getTableName();
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
        String table = getTableName();
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

    protected Connection getConnection() {
        return getConnection(false);
    }

    {{#metadata.useSpring}}
    protected Connection getConnection(boolean isSelect) {
        if(isSelect && !slaveDataSources.isEmpty() && !isActualTransactionActive()){
            try {
               String name;
               if(slaveDataSources.size() == 1){
                   name = slaveDataSources.get(0);
               } else {
                   name = slaveDataSources.get((int) (counter.incrementAndGet() % slaveDataSources.size()));
               }
               if(getLogger().isDebugEnabled()){
                   getLogger().debug("Use slave dataSource {}",name);
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
    {{^metadata.shard}}
    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired{{/metadata.useSpring}}
    {{#metadata.dataSource}}@org.springframework.beans.factory.annotation.Qualifier("{{metadata.dataSource}}"){{/metadata.dataSource}}
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired{{/metadata.useSpring}}
    public void setSlaveDataSourceMap(Map<String, DataSource> slaveDataSourceMap) {
        this.dataSourceMap = slaveDataSourceMap;
        {{#metadata.slaveDataSources}}
        if(slaveDataSourceMap.get("{{.}}") != null){
            slaveDataSources.add("{{.}}");
        }
        {{/metadata.slaveDataSources}}
    }
    {{/metadata.shard}}

    public DataSource getDataSource() {
        return dataSource;
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


    public {{metadata.typeHandlerClazzName}} getDefaultTypeHandler() {
        return defaultTypeHandler;
    }

    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired(required = false){{/metadata.useSpring}}
    public void setDefaultTypeHandler({{metadata.typeHandlerClazzName}} defaultTypeHandler) {
        this.defaultTypeHandler = defaultTypeHandler;
    }

    protected org.slf4j.Logger getLogger(){
        return log;
    }
}
