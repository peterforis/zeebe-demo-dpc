package com.example.zeebedemo;


import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ZeebeDemoResponseProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @Value("${demokey}")
    private String demoKey;

    public void response() {
        Map<String, Object> variables = new HashMap<>();
        int randomInt = (int) (Math.random() * 10);
        variables.put("hellonumber", randomInt);
        System.out.println(demoKey);
        zeebeClient.newPublishMessageCommand()
                .messageName("hello-message")
                .correlationKey(demoKey)
                .timeToLive(Duration.ofMillis(1000))
                .variables(variables)
                .send();
        logger.info("Successfully published response");


    }

}
