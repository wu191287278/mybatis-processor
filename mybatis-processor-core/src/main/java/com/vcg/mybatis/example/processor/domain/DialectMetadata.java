package com.vcg.mybatis.example.processor.domain;

public class DialectMetadata {

    private String leftEscape = "";

    private String rightEscape = "";

    private String exampleJavaTemplatePath = "templates/Example.java";

    private String exampleXmlTemplatePath = "templates/Example.xml";

    private String jqlXmlTemplatePath = "templates/Example.xml";

    private String queryTemplatePath = "templates/Query.java";

    public String getLeftEscape() {
        return leftEscape;
    }

    public DialectMetadata setLeftEscape(String leftEscape) {
        this.leftEscape = leftEscape;
        return this;
    }

    public String getRightEscape() {
        return rightEscape;
    }

    public DialectMetadata setRightEscape(String rightEscape) {
        this.rightEscape = rightEscape;
        return this;
    }

    public String getExampleJavaTemplatePath() {
        return exampleJavaTemplatePath;
    }

    public DialectMetadata setExampleJavaTemplatePath(String exampleJavaTemplatePath) {
        this.exampleJavaTemplatePath = exampleJavaTemplatePath;
        return this;
    }

    public String getExampleXmlTemplatePath() {
        return exampleXmlTemplatePath;
    }

    public DialectMetadata setExampleXmlTemplatePath(String exampleXmlTemplatePath) {
        this.exampleXmlTemplatePath = exampleXmlTemplatePath;
        return this;
    }

    public String getJqlXmlTemplatePath() {
        return jqlXmlTemplatePath;
    }

    public DialectMetadata setJqlXmlTemplatePath(String jqlXmlTemplatePath) {
        this.jqlXmlTemplatePath = jqlXmlTemplatePath;
        return this;
    }

    public String getQueryTemplatePath() {
        return queryTemplatePath;
    }

    public DialectMetadata setQueryTemplatePath(String queryTemplatePath) {
        this.queryTemplatePath = queryTemplatePath;
        return this;
    }
}
