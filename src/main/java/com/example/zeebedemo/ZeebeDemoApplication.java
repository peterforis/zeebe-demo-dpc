package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RestController
public class ZeebeDemoApplication {

	private Logger LOG = LoggerFactory.getLogger(ZeebeDemoApplication.class);

	private ZeebeClient client;

//	TODO: @Value
	private String gatewayAddress = "127.0.0.1:26500";

    public static void main(String[] args) {
        SpringApplication.run(ZeebeDemoApplication.class, args);
    }

    @PostConstruct
    public void setup() {
        client = ZeebeClient.newClientBuilder()
                .gatewayAddress(gatewayAddress)
                .usePlaintext()
                .build();
        LOG.info("Zeebe client connected to {}", gatewayAddress);
    }

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		doZeebe();
    	return String.format("Hello %s!\n", name);
    }

    public void doZeebe() {

        final DeploymentEvent deployment = client.newDeployCommand()
                .addResourceFromClasspath("hello-process.bpmn")
                .send()
                .join();

        final int version = deployment.getProcesses().get(0).getVersion();
        LOG.info("Workflow deployed. Version: " + version);

        final ProcessInstanceEvent processInstanceEvent = client.newCreateInstanceCommand()
                .bpmnProcessId("HelloProcess")
                .latestVersion()
                .send()
                .join();

        final long processInstanceKey = processInstanceEvent.getProcessInstanceKey();
        LOG.info("Workflow instance created. Key: " + processInstanceKey);

        client.close();
        LOG.info("Closed.");
    }

}
