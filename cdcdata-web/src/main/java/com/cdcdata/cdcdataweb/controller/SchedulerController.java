package com.cdcdata.cdcdataweb.controller;

import com.cdcdata.cdcdataweb.scheduler.AsycTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SchedulerController {
    @Autowired
    AsycTask asycTask;

    @RequestMapping("/task")
    @ResponseBody
    public String sayHello() throws Exception{
        long start = System.currentTimeMillis();
        asycTask.task1();
        asycTask.task2();
        asycTask.task3();
        long end = System.currentTimeMillis();
        System.out.println("task use: "+(end-start));
        return "Hello sayHello";
    }

}
