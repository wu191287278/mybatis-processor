package com.vcg.mybatis.example.processor.parser;

import java.util.List;

public class MethodMetadata {

    private String name;

    private List<String> parameterTypes;

    private String returnType;


    public String getName() {
        return name;
    }

    public MethodMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public MethodMetadata setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public String getReturnType() {
        return returnType;
    }

    public MethodMetadata setReturnType(String returnType) {
        this.returnType = returnType;
        return this;
    }
}
