package com.zhiyong.gateway.server.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @ClassName ThreadPoolConfiguration
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/1 下午11:51
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean
    public Executor threadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("monitor-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }
}
