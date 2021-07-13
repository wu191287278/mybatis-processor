package com.vcg.mybatis.example.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Field;
import java.util.List;

import com.vcg.mybatis.example.processor.handler.NumberEnum;
import com.vcg.mybatis.example.processor.handler.NumberEnumTypeHandler;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

@Order(0)
public class JqlConfiguration implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean;
            JqlParser.parse(sqlSessionFactory);
            List<Interceptor> interceptors = sqlSessionFactory.getConfiguration().getInterceptors();
            if (interceptors != null) {
                boolean containsJqlPageInterceptor = interceptors.stream().anyMatch(i -> JqlInterceptor.class.isAssignableFrom(i.getClass()));
                boolean containsPersistenceInterceptor = interceptors.stream().anyMatch(i -> PersistenceInterceptor.class.isAssignableFrom(i.getClass()));
                boolean containsShowSlowSqlInterceptor = interceptors.stream().anyMatch(i -> ShowSlowSqlInterceptor.class.isAssignableFrom(i.getClass()));
                Configuration configuration = sqlSessionFactory.getConfiguration();

                if (!containsJqlPageInterceptor) {
                    configuration.addInterceptor(new JqlInterceptor());
                }
                if (!containsPersistenceInterceptor) {
                    configuration.addInterceptor(new PersistenceInterceptor());
                }
                if (!containsShowSlowSqlInterceptor) {
                    configuration.addInterceptor(ShowSlowSqlInterceptor.INSTANCE);
                }
            }
            Configuration configuration = sqlSessionFactory.getConfiguration();
            for (Object resultMap : configuration.getResultMaps()) {
                if (resultMap instanceof ResultMap) {
                    registerTypeHandler((ResultMap) resultMap);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private static void registerTypeHandler(ResultMap resultMap) {
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        for (ResultMapping resultMapping : resultMappings) {
            Class<?> javaType = resultMapping.getJavaType();
            if (NumberEnum.class.isAssignableFrom(javaType)) {
                try {
                    Field handler = resultMapping.getClass().getDeclaredField("typeHandler");
                    handler.setAccessible(true);
                    handler.set(resultMapping, new NumberEnumTypeHandler(javaType));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
