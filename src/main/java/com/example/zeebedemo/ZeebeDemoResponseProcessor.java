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

/**
 * This class is responsible for generating the response necessary to advance receive tasks in the bpmn workflow.
 */
@Component
public class ZeebeDemoResponseProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @Value("${demokey}")
    private String demoKey;

    /**
     * This method publishes the response method for the receive task in the bpmn. First, it initializes a map, in
     * which it can store variables we want to pass back to Zeebe, and adds a random integer, mapped to the key
     * hellonumber. This will be used to decide which path to take at the exclusive gateway in the bpmn.
     * It then publishes this message, with the randomly generated String demoKey as the correlation key. Zeebe will
     * then use the correlation key, to identify which instance of the workflow a particular response belongs to.
     * It does this, by comparing the value of demoKey that we send as a correlation key, to the value part of key-value
     * pairs in the variable map of various instances, where the key is also the string "demoKey". If it finds a match,
     * it will correlate this reponse to that instance.
     */
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
