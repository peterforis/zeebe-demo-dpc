package com.example.zeebedemo;


import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZeebeDemoResponseProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    public void response(){
        Map<String, Object> variables = new HashMap<>();
        int randomInt = (int) (Math.random() * (10 - 1));
        variables.put("hellonumber", randomInt);
        String demoKey = "demoKey";
            zeebeClient.newPublishMessageCommand()
                    .messageName("hello-message")
                    .correlationKey(demoKey)
                    .timeToLive(Duration.ofMillis(1000))
                    .variables(variables)
                    .send();
        logger.info("Successfully published response");






    }

}
