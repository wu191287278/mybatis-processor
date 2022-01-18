package com.vcg.mybatis.example.processor.handler;

import java.io.Serializable;

public interface NumberEnum<E extends Enum> extends Serializable {

    Number getNumberValue();

}
