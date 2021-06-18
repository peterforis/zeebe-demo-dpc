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

@SpringBootApplication
@RestController
public class ZeebeDemoApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ZeebeDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZeebeDemoApplication.class, args);
    }

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		doZeebe();
    	return String.format("Hello %s!", name);
    }

    public void doZeebe() {
        final ZeebeClient client = ZeebeClient.newClientBuilder()
                .gatewayAddress("127.0.0.1:26500")
                .usePlaintext()
                .build();
        LOG.info("Connected.");

        final DeploymentEvent deployment = client.newDeployCommand()
                .addResourceFromClasspath("order-process.bpmn")
                .send()
                .join();

        final int version = deployment.getProcesses().get(0).getVersion();
        LOG.info("Workflow deployed. Version: " + version);

        final WorkflowInstanceEvent wfInstance = client.newCreateInstanceCommand()
                .bpmnProcessId("order-process")
                .latestVersion()
                .send()
                .join();

        final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();

        System.out.println("Workflow instance created. Key: " + workflowInstanceKey);


        client.close();
        LOG.info("Closed.");
    }

}
