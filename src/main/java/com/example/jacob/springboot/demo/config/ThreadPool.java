package com.example.jacob.springboot.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * @author duhongbo
 * @date 2020/3/18 15:52
 */
@Configuration
public class ThreadPool {

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        threadPoolTaskExecutor.setCorePoolSize(availableProcessors);
        threadPoolTaskExecutor.setMaxPoolSize(availableProcessors<<3);
        threadPoolTaskExecutor.setQueueCapacity(availableProcessors<<4);
        threadPoolTaskExecutor.setKeepAliveSeconds(200);
        // 直接抛弃也不抛出异常
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("Convert-Thread-");
        return threadPoolTaskExecutor;
    }
}
