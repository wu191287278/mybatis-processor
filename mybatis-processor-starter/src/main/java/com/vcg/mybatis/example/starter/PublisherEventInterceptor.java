package com.vcg.mybatis.example.starter;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
public class PublisherEventInterceptor implements Interceptor {

    private ApplicationContext applicationContext;

    private Map<String, Class> typeCache = new ConcurrentHashMap<>();

    public PublisherEventInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Object parameter = args[1];
        MappedStatement ms = (MappedStatement) args[0];
        Object proceed = invocation.proceed();
        String baseResultMap = ms.getId().substring(0, ms.getId().lastIndexOf(".")) + ".BaseResultMap";
        if (typeCache.get(baseResultMap) != null) {
            Class type = typeCache.get(baseResultMap);
            Event event = new Event(ms.getId(), ms.getSqlCommandType().toString(), type, proceed, parameter);
            this.applicationContext.publishEvent(event);
        } else {
            Configuration configuration = ms.getConfiguration();
            if (configuration.getResultMapNames().contains(baseResultMap)) {
                ResultMap resultMap = configuration.getResultMap(baseResultMap);
                Class<?> type = resultMap.getType();
                typeCache.put(ms.getId(), type);
                Event event = new Event(ms.getId(), ms.getSqlCommandType().toString(), type, proceed, parameter);
                this.applicationContext.publishEvent(event);
            }
        }

        return proceed;

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


}
