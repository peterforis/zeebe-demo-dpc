package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class WorkerManager {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @PostConstruct
    public void setup() {
        zeebeClient.newWorker().jobType("hello-world").handler((client, job) -> {
            LOG.info("Hello from worker");
            client.newCompleteCommand(job.getKey())
                    .send();
        }).open();
    }
}
