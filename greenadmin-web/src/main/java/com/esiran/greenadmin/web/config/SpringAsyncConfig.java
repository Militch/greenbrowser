package com.esiran.greenadmin.web.config;

import com.esiran.greenadmin.web.tasks.BackendAsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class SpringAsyncConfig {
    @Bean
    public Executor taskExecutor() {
        return Executors.newCachedThreadPool();
    }
}
