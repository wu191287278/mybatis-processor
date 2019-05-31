package com.vcg.mybatis.example.starter;

import com.vcg.mybatis.example.processor.util.CamelUtils;
import org.springframework.context.ApplicationEvent;

import javax.persistence.Table;

public class Event extends ApplicationEvent {

    private String mappedStatementId;

    private String sqlCommandType;

    private Class type;

    private Object result;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public Event(String mappedStatementId,
                 String sqlCommandType,
                 Class type,
                 Object result,
                 Object source) {
        super(source);
        this.sqlCommandType = sqlCommandType;
        this.mappedStatementId = mappedStatementId;
        this.type = type;
        this.result = result;
    }


    public String getSqlCommandType() {
        return sqlCommandType;
    }

    public String getMappedStatementId() {
        return mappedStatementId;
    }

    public Class getType() {
        return type;
    }

    public String getTableName() {
        Table annotation = (Table) getType().getAnnotation(Table.class);
        return annotation == null ? CamelUtils.toSnake(this.type.getSimpleName()) : annotation.name();
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "MybatisUpdateEvent{" +
                "mappedStatementId='" + mappedStatementId + '\'' +
                ", sqlCommandType='" + sqlCommandType + '\'' +
                ", type=" + type +
                ", result=" + result +
                ", source=" + source +
                '}';
    }
}
