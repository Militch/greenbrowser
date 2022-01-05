package tech.xfs.xfschainexplorer.web.config;

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
