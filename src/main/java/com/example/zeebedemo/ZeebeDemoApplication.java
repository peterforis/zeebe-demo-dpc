package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!\n", name);
    }

    public void doZeebe() {

        final ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("HelloProcess")
                .latestVersion()
                .send()
                .join();

        final long processInstanceKey = processInstanceEvent.getProcessInstanceKey();
        LOG.info("Workflow instance created. Key: " + processInstanceKey);
    }

}
