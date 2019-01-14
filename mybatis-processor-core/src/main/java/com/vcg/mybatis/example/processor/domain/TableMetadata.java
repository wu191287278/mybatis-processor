package com.vcg.mybatis.example.processor.domain;

import java.util.ArrayList;
import java.util.List;

public class TableMetadata {

    private String tableName;

    private String repositoryClazzName;

    private String domainClazzName;

    private String exampleClazzName;

    private String repositoryClazzSimpleName;

    private String domainClazzSimpleName;

    private String exampleClazzSimpleName;

    private boolean dynamicField = false;

    private ColumnMetadata primaryMetadata;

    private List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    private String columns;

    private String packageName;

    private ColumnMetadata partitionKey;

    private Integer shard;

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

    public boolean isDynamicField() {
        return dynamicField;
    }

    public TableMetadata setDynamicField(boolean dynamicField) {
        this.dynamicField = dynamicField;
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

    public TableMetadata setShard(Integer shard) {
        this.shard = shard;
        return this;
    }

    public Integer getShard() {
        return shard;
    }


    public ColumnMetadata getPartitionKey() {
        return partitionKey;
    }

    public TableMetadata setPartitionKey(ColumnMetadata partitionKey) {
        this.partitionKey = partitionKey;
        return this;
    }
}
