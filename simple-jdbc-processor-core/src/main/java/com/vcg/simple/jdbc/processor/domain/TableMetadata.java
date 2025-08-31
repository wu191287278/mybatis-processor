package com.vcg.simple.jdbc.processor.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableMetadata {

    private String tableName;

    private String originTableName;

    private String repositoryClazzName;

    private String domainClazzName;

    private String exampleClazzName;

    private String repositoryClazzSimpleName;

    private String domainClazzSimpleName;

    private String exampleClazzSimpleName;

    private ColumnMetadata primaryMetadata;

    private List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    private String columns;

    private String packageName;

    private ColumnMetadata partitionKey;

    private Long randomId = new Random().nextLong();

    private String rightEncode = "";

    private String leftEncode = "";

    private boolean useSpring;

    private String dataSource;

    private List<String> slaveDataSources;

    private String shardRepositoryClazzSimpleName;

    private String shardRepositoryClazzName;

    private String typeHandlerClazzSimpleName;

    private String typeHandlerClazzName;

    private boolean shard;


    public String getTableName() {
        return tableName;
    }

    public TableMetadata setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getRepositoryClazzName() {
        return repositoryClazzName;
    }

    public TableMetadata setRepositoryClazzName(String repositoryClazzName) {
        this.repositoryClazzName = repositoryClazzName;
        if (repositoryClazzName != null) {
            String[] split = repositoryClazzName.split("[.]");
            this.repositoryClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public String getDomainClazzName() {
        return domainClazzName;
    }

    public TableMetadata setDomainClazzName(String domainClazzName) {
        this.domainClazzName = domainClazzName;
        if (domainClazzName != null) {
            String[] split = domainClazzName.split("[.]");
            this.domainClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public String getExampleClazzName() {
        return exampleClazzName;
    }

    public TableMetadata setExampleClazzName(String exampleClazzName) {
        this.exampleClazzName = exampleClazzName;
        if (exampleClazzName != null) {
            String[] split = exampleClazzName.split("[.]");
            this.exampleClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public ColumnMetadata getPrimaryMetadata() {
        return primaryMetadata;
    }

    public TableMetadata setPrimaryMetadata(ColumnMetadata primaryMetadata) {
        this.primaryMetadata = primaryMetadata;
        return this;
    }

    public List<ColumnMetadata> getColumnMetadataList() {
        return columnMetadataList;
    }

    public TableMetadata setColumnMetadataList(List<ColumnMetadata> columnMetadataList) {
        this.columnMetadataList = columnMetadataList;
        return this;
    }

    public String getRepositoryClazzSimpleName() {
        return repositoryClazzSimpleName;
    }

    public TableMetadata setRepositoryClazzSimpleName(String repositoryClazzSimpleName) {
        this.repositoryClazzSimpleName = repositoryClazzSimpleName;
        return this;
    }

    public String getDomainClazzSimpleName() {
        return domainClazzSimpleName;
    }

    public TableMetadata setDomainClazzSimpleName(String domainClazzSimpleName) {
        this.domainClazzSimpleName = domainClazzSimpleName;
        return this;
    }

    public String getExampleClazzSimpleName() {
        return exampleClazzSimpleName;
    }

    public TableMetadata setExampleClazzSimpleName(String exampleClazzSimpleName) {
        this.exampleClazzSimpleName = exampleClazzSimpleName;
        return this;
    }

    public String getColumns() {
        return columns;
    }

    public TableMetadata setColumns(String columns) {
        this.columns = columns;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public TableMetadata setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }


    public ColumnMetadata getPartitionKey() {
        return partitionKey;
    }

    public TableMetadata setPartitionKey(ColumnMetadata partitionKey) {
        this.partitionKey = partitionKey;
        return this;
    }

    public Long getRandomId() {
        return randomId;
    }

    public void setRandomId(Long randomId) {
        this.randomId = randomId;
    }

    public String getRightEncode() {
        return rightEncode;
    }

    public TableMetadata setRightEncode(String rightEncode) {
        this.rightEncode = rightEncode;
        return this;
    }

    public String getLeftEncode() {
        return leftEncode;
    }

    public TableMetadata setLeftEncode(String leftEncode) {
        this.leftEncode = leftEncode;
        return this;
    }

    public String getOriginTableName() {
        return originTableName;
    }

    public TableMetadata setOriginTableName(String originTableName) {
        this.originTableName = originTableName;
        return this;
    }

    public boolean isUseSpring() {
        return useSpring;
    }

    public TableMetadata setUseSpring(boolean useSpring) {
        this.useSpring = useSpring;
        return this;
    }

    public TableMetadata setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public String getDataSource() {
        return dataSource;
    }

    public List<String> getSlaveDataSources() {
        return slaveDataSources;
    }

    public TableMetadata setSlaveDataSources(List<String> slaveDataSources) {
        this.slaveDataSources = slaveDataSources;
        return this;
    }

    public String getShardRepositoryClazzSimpleName() {
        return shardRepositoryClazzSimpleName;
    }

    public void setShardRepositoryClazzSimpleName(String shardRepositoryClazzSimpleName) {
        this.shardRepositoryClazzSimpleName = shardRepositoryClazzSimpleName;
    }

    public String getShardRepositoryClazzName() {
        return shardRepositoryClazzName;
    }

    public TableMetadata setShardRepositoryClazzName(String shardRepositoryClazzName) {
        this.shardRepositoryClazzName = shardRepositoryClazzName;
        if (shardRepositoryClazzName != null) {
            String[] split = shardRepositoryClazzName.split("[.]");
            this.shardRepositoryClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public String getTypeHandlerClazzName() {
        return typeHandlerClazzName;
    }

    public TableMetadata setTypeHandlerClazzName(String typeHandlerClazzName) {
        this.typeHandlerClazzName = typeHandlerClazzName;
        if (typeHandlerClazzName != null) {
            String[] split = typeHandlerClazzName.split("[.]");
            this.typeHandlerClazzSimpleName = split[split.length - 1];
        }
        return this;
    }

    public String getTypeHandlerClazzSimpleName() {
        return typeHandlerClazzSimpleName;
    }

    public void setTypeHandlerClazzSimpleName(String typeHandlerClazzSimpleName) {
        this.typeHandlerClazzSimpleName = typeHandlerClazzSimpleName;
    }

    public boolean isShard() {
        return shard;
    }

    public TableMetadata setShard(boolean shard) {
        this.shard = shard;
        return this;
    }
}
