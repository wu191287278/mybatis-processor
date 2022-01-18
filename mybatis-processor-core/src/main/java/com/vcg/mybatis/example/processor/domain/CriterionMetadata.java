package com.vcg.mybatis.example.processor.domain;

public class CriterionMetadata {

    private String firstUpFieldName;

    private String fieldName;

    private String fieldAliasName;

    private String firstUpFieldAliasName;

    private String javaType;

    private String javaDoc;

    private boolean like;

    private boolean notLike;

    private boolean in;

    private boolean notIn;

    private boolean equalTo;

    private boolean notEqualTo;

    private boolean greaterThan;

    private boolean lessThan;

    private boolean greaterThanOrEqualTo;

    private boolean lessThanOrEqualTo;

    private boolean single = true;

    private boolean collection;

    private boolean between;

    private String dateFormat;

    private boolean numberFormat;

    public String getFirstUpFieldName() {
        return firstUpFieldName;
    }

    public void setFirstUpFieldName(String firstUpFieldName) {
        this.firstUpFieldName = firstUpFieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        if (fieldName != null) {
            if (fieldName.length() == 1) {
                this.firstUpFieldName = fieldName.toUpperCase();
            } else {
                this.firstUpFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
        }
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        String[] split = javaType.split("\\s+");
        this.javaType = split[split.length - 1];
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isNotLike() {
        return notLike;
    }

    public void setNotLike(boolean notLike) {
        this.notLike = notLike;
    }

    public boolean isIn() {
        return in;
    }

    public void setIn(boolean in) {
        this.in = in;
        this.collection = true;
        this.single = false;
        this.between = false;
    }

    public boolean isNotIn() {
        return notIn;
    }

    public void setNotIn(boolean notIn) {
        this.notIn = notIn;
        this.collection = true;
        this.single = false;
        this.between = false;
    }

    public boolean isEqualTo() {
        return equalTo;
    }

    public void setEqualTo(boolean equalTo) {
        this.equalTo = equalTo;
    }

    public boolean isNotEqualTo() {
        return notEqualTo;
    }

    public void setNotEqualTo(boolean notEqualTo) {
        this.notEqualTo = notEqualTo;
    }

    public boolean isGreaterThan() {
        return greaterThan;
    }

    public void setGreaterThan(boolean greaterThan) {
        this.greaterThan = greaterThan;
    }

    public boolean isLessThan() {
        return lessThan;
    }

    public void setLessThan(boolean lessThan) {
        this.lessThan = lessThan;
    }

    public boolean isGreaterThanOrEqualTo() {
        return greaterThanOrEqualTo;
    }

    public void setGreaterThanOrEqualTo(boolean greaterThanOrEqualTo) {
        this.greaterThanOrEqualTo = greaterThanOrEqualTo;
    }

    public boolean isLessThanOrEqualTo() {
        return lessThanOrEqualTo;
    }

    public void setLessThanOrEqualTo(boolean lessThanOrEqualTo) {
        this.lessThanOrEqualTo = lessThanOrEqualTo;
    }

    public String getFieldAliasName() {
        return fieldAliasName;
    }

    public void setFieldAliasName(String fieldAliasName) {
        this.fieldAliasName = fieldAliasName;
        if (fieldAliasName != null) {
            if (fieldAliasName.length() == 1) {
                this.firstUpFieldAliasName = fieldAliasName.toUpperCase();
            } else {
                this.firstUpFieldAliasName = fieldAliasName.substring(0, 1).toUpperCase() + fieldAliasName.substring(1);
            }
        }
    }

    public String getFirstUpFieldAliasName() {
        return firstUpFieldAliasName;
    }

    public void setFirstUpFieldAliasName(String firstUpFieldAliasName) {
        this.firstUpFieldAliasName = firstUpFieldAliasName;
    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public CriterionMetadata setJavaDoc(String javaDoc) {

        this.javaDoc = javaDoc;
        if (javaDoc != null && javaDoc.length() > 1) {
            this.javaDoc = javaDoc.substring(0, javaDoc.length() - 1);
        }
        return this;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public boolean isBetween() {
        return between;
    }

    public void setBetween(boolean between) {
        this.between = between;
        this.single = false;
        this.collection = false;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(boolean numberFormat) {
        this.numberFormat = numberFormat;
    }
}
