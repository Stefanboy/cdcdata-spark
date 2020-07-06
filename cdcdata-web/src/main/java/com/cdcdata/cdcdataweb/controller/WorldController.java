package com.cdcdata.cdcdataweb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController = @Controller+@ResponseBody
 * 点击@RestController看看源码就明白
 */
@RestController
public class WorldController {
    @RequestMapping("/sayWorld")
    public String sayWorld(){
        return "World cdcdata";
    }
}
