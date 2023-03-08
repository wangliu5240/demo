package com.example.demo.TestJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@EnableScheduling
@ConditionalOnProperty(prefix = "sysdocking.job",name = "enable",havingValue = "true", matchIfMissing = false)
public class TestJob {

    private final static Logger logger =  LoggerFactory.getLogger(TestJob.class);

    @Scheduled(cron ="0/5 * * * * ?")
    public void jobTest(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("定时器测试：" + format.format(new Date()));
    }
}
