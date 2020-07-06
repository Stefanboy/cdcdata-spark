package com.cdcdata.cdcdataweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SB的入口类
 */
@SpringBootApplication
//@EnableScheduling
//@EnableAsync
public class CdcdataWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdcdataWebApplication.class, args);
    }

}
