package com.vcg.simple.jdbc.processor.domain;

public class DialectMetadata {

    private String leftEscape = "";

    private String rightEscape = "";

    private String exampleJavaTemplatePath = "templates/Example.java";

    private String repositoryTemplatePath = "templates/SimpleJdbcRepository.java";

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
}
