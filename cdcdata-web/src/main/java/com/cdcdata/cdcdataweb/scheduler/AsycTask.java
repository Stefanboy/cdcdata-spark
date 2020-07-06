package com.cdcdata.cdcdataweb.scheduler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async//异步注解 意味着该类中的所有方法都是异步的
public class AsycTask {

    public void task1() throws Exception{
        long start = System.currentTimeMillis();
        Thread.sleep(1000);
        long end = System.currentTimeMillis();
        System.out.println("task1 use: "+(end-start));

    }
    public void task2() throws Exception{
        long start = System.currentTimeMillis();
        Thread.sleep(2000);
        long end = System.currentTimeMillis();
        System.out.println("task2 use: "+(end-start));

    }
    public void task3() throws Exception{
        long start = System.currentTimeMillis();
        Thread.sleep(3000);
        long end = System.currentTimeMillis();
        System.out.println("task3 use: "+(end-start));

    }
}
