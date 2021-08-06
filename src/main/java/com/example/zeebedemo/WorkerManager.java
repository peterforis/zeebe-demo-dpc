package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class WorkerManager {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @PostConstruct
    public void setup() {
        zeebeClient.newWorker().jobType("hello-world").handler((client, job) -> {
            Map<String,Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Hello {}", incomingVariables.get("name"));
            String message = "I am a message" + incomingVariables.get("name");
            incomingVariables.put("message",message);
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();



        zeebeClient.newWorker().jobType("hello-default").handler((client, job) -> {
            Map<String,Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Number received {}", incomingVariables.get("hellonumber"));
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();



        zeebeClient.newWorker().jobType("hello-condition").handler((client, job) -> {
            Map<String,Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Number received {}", incomingVariables.get("hellonumber"));
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();



        zeebeClient.newWorker().jobType("hello-checker").handler((client, job) -> {
            Map<String,Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Successfully received response");
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();
    }
}
