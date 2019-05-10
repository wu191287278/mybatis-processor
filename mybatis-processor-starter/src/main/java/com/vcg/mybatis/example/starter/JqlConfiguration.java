package com.vcg.mybatis.example.starter;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

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
                Configuration configuration = sqlSessionFactory.getConfiguration();

                if (!containsJqlPageInterceptor) {
                    configuration.addInterceptor(new JqlInterceptor());
                }
                if (!containsPersistenceInterceptor) {
                    configuration.addInterceptor(new PersistenceInterceptor());
                }
            }
        }
        return bean;
    }

}
