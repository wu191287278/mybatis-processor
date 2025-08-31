package {{metadata.packageName}};


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class {{metadata.shardRepositoryClazzSimpleName}} {

    private final Map<String, {{metadata.repositoryClazzName}}> repositoryMap = new ConcurrentHashMap<>();

    private {{metadata.typeHandlerClazzName}} defaultTypeHandler = new {{metadata.typeHandlerClazzName}}();

    private DataSource dataSource;

    private Map<String, DataSource> dataSourceMap;

    public {{metadata.domainClazzName}} selectByPrimaryKey({{metadata.domainClazzName}} t) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.selectByPrimaryKey(t.getId());
    }

    public {{metadata.domainClazzName}} selectByPrimaryKeyForUpdate({{metadata.domainClazzName}} t) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.selectByPrimaryKeyForUpdate(t.getId());
    }

    public List<{{metadata.domainClazzName}}> selectByPrimaryKeys(List<{{metadata.domainClazzName}}> ts) {
        Map<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> groupMap = groupMap(ts);
        List<{{metadata.domainClazzName}}> list = new ArrayList<>();
        for (Map.Entry<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> entry : groupMap.entrySet()) {
            {{metadata.repositoryClazzName}} repository = entry.getKey();
            List<{{metadata.primaryMetadata.javaType}}> ids = entry.getValue()
                    .stream()
                    .map({{metadata.domainClazzName}}::get{{metadata.primaryMetadata.firstUpFieldName}})
                    .collect(Collectors.toList());
            List<{{metadata.domainClazzName}}> shardResult = repository.selectByPrimaryKeys(ids);
            list.addAll(shardResult);
        }
        return list;
    }

    public List<{{metadata.domainClazzName}}> selectByExample({{metadata.domainClazzName}} t, {{metadata.exampleClazzName}} example) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.selectByExample(example);
    }

    public void insertSelective({{metadata.domainClazzName}} t) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        repository.insertSelective(t);
    }

    public void insertBatch(List<{{metadata.domainClazzName}}> ts) {
        Map<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> groupMap = groupMap(ts);
        for (Map.Entry<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> entry : groupMap.entrySet()) {
            entry.getKey().insertBatch(entry.getValue());
        }
    }

    public long updateByPrimaryKeySelective({{metadata.domainClazzName}} t) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.updateByPrimaryKeySelective(t);
    }

    public long updateByExampleSelective({{metadata.domainClazzName}} t, {{metadata.exampleClazzName}} example) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.updateByExampleSelective(t, example);
    }

    public long deleteByPrimaryKey({{metadata.domainClazzName}} t) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.deleteByPrimaryKey(t.getId());
    }

    public long deleteByPrimaryKeys(List<{{metadata.domainClazzName}}> ts) {
        Map<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> groupMap = groupMap(ts);
        long deleted = 0;
        for (Map.Entry<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> entry : groupMap.entrySet()) {
            {{metadata.repositoryClazzName}} repository = entry.getKey();
            List<{{metadata.primaryMetadata.javaType}}> ids = entry.getValue()
                    .stream()
                    .map({{metadata.domainClazzName}}::get{{metadata.primaryMetadata.firstUpFieldName}})
                    .collect(Collectors.toList());
            deleted = deleted + repository.deleteByPrimaryKeys(ids);
        }
        return deleted;
    }

    public long deleteByExample({{metadata.domainClazzName}} t, {{metadata.exampleClazzName}} example) {
        {{metadata.repositoryClazzName}} repository = getRepository(t);
        return repository.deleteByExample(example);
    }


    protected {{metadata.repositoryClazzName}} getRepository({{metadata.domainClazzName}} t) {
        String tableName = getTableName(t);
        return repositoryMap.computeIfAbsent(tableName, r -> {
            {{metadata.repositoryClazzName}} repository = new {{metadata.repositoryClazzName}}(this.getClass(), r, getDataSource(t), getSlaveDataSourceMap(t), this.defaultTypeHandler) {
            };
            return repository;
        });
    }

    protected abstract String getTableName({{metadata.domainClazzName}} t);

    protected Map<String, DataSource> getSlaveDataSourceMap({{metadata.domainClazzName}} t){
        return this.dataSourceMap;
    }

    protected DataSource getDataSource({{metadata.domainClazzName}} t){
        return this.dataSource;
    }

    protected Map<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> groupMap(List<{{metadata.domainClazzName}}> ts) {
        Map<{{metadata.repositoryClazzName}}, List<{{metadata.domainClazzName}}>> groupMap = new LinkedHashMap<>();
        for ({{metadata.domainClazzName}} t : ts) {
            {{metadata.repositoryClazzName}} repository = getRepository(t);
            List<{{metadata.domainClazzName}}> shardList = groupMap.computeIfAbsent(repository, k -> new ArrayList<>());
            shardList.add(t);
        }
        return groupMap;
    }

    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired(required = false){{/metadata.useSpring}}
    public void setDefaultTypeHandler({{metadata.typeHandlerClazzName}} defaultTypeHandler) {
        this.defaultTypeHandler = defaultTypeHandler;
    }

    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired{{/metadata.useSpring}}
    {{#metadata.dataSource}}@org.springframework.beans.factory.annotation.Qualifier("{{metadata.dataSource}}"){{/metadata.dataSource}}
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    {{#metadata.useSpring}}@org.springframework.beans.factory.annotation.Autowired{{/metadata.useSpring}}
    public void setSlaveDataSourceMap(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }
}
