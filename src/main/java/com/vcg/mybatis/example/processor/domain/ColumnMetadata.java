package com.vcg.mybatis.example.processor.domain;

public class ColumnMetadata {

    private String fieldName;

    private String columnName;

    private String firstUpFiledName;

    private String jdbcType;

    private String javaType;

    private boolean primary;

    private boolean useGeneratedKeys;

    public String getFieldName() {
        return fieldName;
    }

    public ColumnMetadata setFieldName(String fieldName) {
        this.fieldName = fieldName;
        if (fieldName != null) {
            if (fieldName.length() == 1) {
                this.firstUpFiledName = fieldName.toUpperCase();
            } else {
                this.firstUpFiledName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
        }
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnMetadata setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public ColumnMetadata setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    public String getJavaType() {
        return javaType;
    }

    public ColumnMetadata setJavaType(String javaType) {
        this.javaType = javaType;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public ColumnMetadata setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public ColumnMetadata setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
        return this;
    }

    public String getFirstUpFiledName() {
        return firstUpFiledName;
    }

    public ColumnMetadata setFirstUpFiledName(String firstUpFiledName) {
        this.firstUpFiledName = firstUpFiledName;
        return this;
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" +
                "fieldName='" + fieldName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", firstUpFiledName='" + firstUpFiledName + '\'' +
                ", jdbcType='" + jdbcType + '\'' +
                ", javaType='" + javaType + '\'' +
                ", primary=" + primary +
                ", useGeneratedKeys=" + useGeneratedKeys +
                '}';
    }
}