package com.vcg.mybatis.example.processor.domain;

public enum DialectEnums {
    NONE(
            new DialectMetadata()
    ),
    MYSQL(
            new DialectMetadata()
                    .setLeftEscape("`")
                    .setRightEscape("`")
    ),

    ;

    private final DialectMetadata value;

    DialectEnums(DialectMetadata value) {
        this.value = value;
    }

    public DialectMetadata getValue() {
        return value;
    }
}
