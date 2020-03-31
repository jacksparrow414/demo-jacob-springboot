package com.example.jacob.springboot.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 全局线程池配置
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
        // 线程池关闭的时候等待所有线程执行完毕
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待60s,如果此时还有线程还没有执行完毕,则强制销毁
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        return threadPoolTaskExecutor;
    }
}
