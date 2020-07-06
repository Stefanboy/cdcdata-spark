package com.cdcdata.cdcdataweb.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component//交给spring管理
public class TaskTest {
    //@Scheduled(fixedDelay = 2000)//不会用点进去看看 表示调度周期
    //@Scheduled(fixedDelayString = "2000")//字符串类型的2s调度一次
    @Scheduled(cron = "*/2 * * * * *")//cron表达式2s执行一次
    public void exec() throws Exception{
        System.out.println("执行定时任务,当前时间:"+new Date());
    }
}
