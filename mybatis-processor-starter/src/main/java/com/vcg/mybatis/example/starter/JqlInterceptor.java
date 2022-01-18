package com.vcg.mybatis.example.starter;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class JqlInterceptor extends PageInterceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];


        //跳过接口已有方法
        if (JqlParser.isIgnoreMethod(ms.getId())) {
            return invocation.proceed();
        }


        //遍历参数 判断是否存在 PageRequest 进行分页
        if (parameter instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) parameter).entrySet()) {
                if (entry.getValue() instanceof PageRequest) {
                    PageRequest request = (PageRequest) entry.getValue();
                    Page page = getPage(invocation, request, JqlParser.isPageResultId(ms.getId()));
                    //mybatis必须返回一个list,否则会报转换异常
                    return Collections.singletonList(new PageWrapper(page.getResult(), request, page.getTotal()));
                }
            }
        }

        //结果集为stream 必须在事务中处理结果集,否则无法遍历数据 具体参考cursor
        if (JqlParser.isStreamResultId(ms.getId())) {
            Executor executor = (Executor) invocation.getTarget();
            RowBounds rowBounds = (RowBounds) args[2];
            Cursor<Object> cursor = executor.queryCursor(ms, parameter, rowBounds);
            return Collections.singletonList(new CursorWrapperToStream<>(cursor));
        }

        Object proceed = invocation.proceed();

        //兼容java8 optional
        if (JqlParser.isOptionalResultId(ms.getId())) {
            if ((proceed instanceof List) && ((List) proceed).size() > 0) {
                Object result = ((List) proceed).get(0);
                if (result instanceof Optional) return proceed;
                return Collections.singletonList(result == null ? Optional.empty() : Optional.of(result));
            }
        }

        if (JqlParser.isBooleanResultId(ms.getId()) && proceed instanceof List) {
            if (((List) proceed).isEmpty()) {
                return Collections.singletonList(false);
            }
        }

        return proceed;
    }


    private Page getPage(Invocation invocation, PageRequest pageRequest, boolean count) throws Throwable {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Page page = PageHelper.startPage(pageNumber, pageSize, count);
        Sort sort = pageRequest.getSort();
        if (!sort.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();
                sb.append(property)
                        .append(" ")
                        .append(direction.isAscending() ? "asc" : "desc")
                        .append(",");
            }
            PageHelper.orderBy(sb.substring(0, sb.length() - 1));
        }
        super.intercept(invocation);
        return page;
    }

}
