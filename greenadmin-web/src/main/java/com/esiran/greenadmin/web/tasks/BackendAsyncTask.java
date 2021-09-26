package com.esiran.greenadmin.web.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BackendAsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(BackendAsyncTask.class);
    @Async
    public void run() throws Exception {
//        logger.info("hello run task");
        while (true) {
            logger.info("hello run task");
            Thread.sleep(10 * 1000);
        }
    }
}
