package com.newgrand.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Data
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "executor")
public class ExecutorConfig {


    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);

    @Value("${spring.task.execution.pool.core-size}")
    private int corePoolSize;
    @Value("${spring.task.execution.pool.max-size}")
    private int maxPoolSize;
    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.task.execution.pool.keep-alive}")
    private int keepAliveSeconds;
    @Value("${spring.task.execution.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "asyncServiceExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        logger.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程空闲时间 (//设置线程的活跃时间 超出时间就会销毁线程)
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //核心线程会一直存活，即使没有任务需要执行。（默认false）时，核心线程会超时关闭
        executor.setAllowCoreThreadTimeOut(false);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 800,
                30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(500));
        return pool;
    }

}