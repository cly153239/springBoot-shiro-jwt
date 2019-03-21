package com.ludi.server.config.pool;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 陆迪
 * @date 2018-10-06 10:54
 * 异步任务线程池
 **/
@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    private final static String THREAD_NAME_PREFIX = "op-async-exec-";
    public static final String OPERATION_ASYNC_EXECUTOR = "OperationAsyncExecutor";

    @Override
    @Bean(value = OPERATION_ASYNC_EXECUTOR)
    public Executor getAsyncExecutor() {
        int corePoolSize = 3;
        int maxPoolSize = 8;
        //队列最大长度
        int queueCapacity = 50;

        SubjectAwareTaskExecutor executor = new SubjectAwareTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}










