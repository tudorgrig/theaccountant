package com.myMoneyTracker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource("spring-config.xml")
@SpringBootApplication
public class MyMoneyTrackerApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(MyMoneyTrackerApplication.class, args);
    }
}
