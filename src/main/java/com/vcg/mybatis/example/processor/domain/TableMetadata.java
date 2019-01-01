package com.vcg.mybatis.example.processor.domain;

import java.util.ArrayList;
import java.util.List;

public class TableMetadata {

    private String tableName;

    private String repositoryClazzName;

    private String domainClazzName;

    private String exampleClazzName;

    private String repositorySimpleName;

    private String domainClazzSimpleName;

    private String exampleClazzSimpleName;

    private boolean dynamicField = false;

    private ColumnMetadata primaryMetadata;

    private List<ColumnMetadata> columnMetadataList = new ArrayList<>();

    private String columns;

    private String packageName;

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
            this.repositorySimpleName = split[split.length - 1];
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

    public String getRepositorySimpleName() {
        return repositorySimpleName;
    }

    public TableMetadata setRepositorySimpleName(String repositorySimpleName) {
        this.repositorySimpleName = repositorySimpleName;
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

    @Override
    public String toString() {
        return "TableMetadata{" +
                "tableName='" + tableName + '\'' +
                ", repositoryClazzName='" + repositoryClazzName + '\'' +
                ", domainClazzName='" + domainClazzName + '\'' +
                ", exampleClazzName='" + exampleClazzName + '\'' +
                ", repositorySimpleName='" + repositorySimpleName + '\'' +
                ", domainClazzSimpleName='" + domainClazzSimpleName + '\'' +
                ", exampleClazzSimpleName='" + exampleClazzSimpleName + '\'' +
                ", dynamicField=" + dynamicField +
                ", primaryMetadata=" + primaryMetadata +
                ", columnMetadataList=" + columnMetadataList +
                ", columns='" + columns + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
