package tech.xfs.xfschainexplorer.web;

import tech.xfs.xfschainexplorer.web.tasks.BackendAsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
    private final BackendAsyncTask backendAsyncTask;

    public AppRunner(BackendAsyncTask backendAsyncTask) {
        this.backendAsyncTask = backendAsyncTask;
    }

    @Override
    public void run(String... args) throws Exception {
        backendAsyncTask.run();
    }
}
