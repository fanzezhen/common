package com.github.fanzezhen.common.magic.doc.yapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zezhen.fan
 */
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.github.fanzezhen.common.magic.doc"})
public class YApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(YApiApplication.class, args);
    }

}
