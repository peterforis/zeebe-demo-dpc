package com.example.zeebedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.UUID;
/*
This class simply starts the Springboot application. It also generates the random ID, that will be used as the demoKey.
*/
@SpringBootApplication
public class ZeebeDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZeebeDemoApplication.class, args);
    }

    private final static UUID app_id = UUID.randomUUID();

    @PostConstruct
    private void init() {
        System.setProperty("demoKey", app_id.toString());
    }
}
