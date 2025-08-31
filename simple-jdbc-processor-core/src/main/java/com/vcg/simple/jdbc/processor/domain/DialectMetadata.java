package com.vcg.simple.jdbc.processor.domain;

public class DialectMetadata {

    private String leftEscape = "";

    private String rightEscape = "";

    private String exampleJavaTemplatePath = "templates/SimpleJdbcExample.java";

    private String repositoryTemplatePath = "templates/SimpleJdbcRepository.java";

    private String shardRepositoryTemplatePath = "templates/ShardSimpleJdbcRepository.java";

    private String typeHandlerTemplatePath = "templates/SimpleJdbcDefaultTypeHandler.java";

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

    public String getRepositoryTemplatePath() {
        return repositoryTemplatePath;
    }

    public void setRepositoryTemplatePath(String repositoryTemplatePath) {
        this.repositoryTemplatePath = repositoryTemplatePath;
    }

    public String getShardRepositoryTemplatePath() {
        return shardRepositoryTemplatePath;
    }

    public void setShardRepositoryTemplatePath(String shardRepositoryTemplatePath) {
        this.shardRepositoryTemplatePath = shardRepositoryTemplatePath;
    }

    public String getTypeHandlerTemplatePath() {
        return typeHandlerTemplatePath;
    }

    public void setTypeHandlerTemplatePath(String typeHandlerTemplatePath) {
        this.typeHandlerTemplatePath = typeHandlerTemplatePath;
    }
}
