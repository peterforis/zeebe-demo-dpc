package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RestController
public class ZeebeDemoApplication {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    public static void main(String[] args) {
        SpringApplication.run(ZeebeDemoApplication.class, args);
    }

    @PostConstruct
    public void setup() {
        zeebeClient.newWorker().jobType("hello-world").handler((client, job) -> {
            LOG.info("Hello from worker");
            client.newCompleteCommand(job.getKey())
                    .send();
        }).open();
    }

    @GetMapping("/start")
    public void start() {
        ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("HelloProcess")
                .latestVersion()
                .send()
                .join();
        long processInstanceKey = processInstanceEvent.getProcessInstanceKey();
        LOG.info("Workflow instance created. Key: " + processInstanceKey);
    }

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!\n", name);
    }

}
