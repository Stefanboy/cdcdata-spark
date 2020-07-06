package com.cdcdata.cdcdataweb.controller;

import com.cdcdata.cdcdataweb.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello(){
        return "Hello sayHello";
    }

    @RequestMapping("/devtools")
    @ResponseBody
    public String devtools(){
        return "devtools sayHello";
    }

    @Autowired
    private Student student;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        System.out.println(student);
        return "test";
    }

}
