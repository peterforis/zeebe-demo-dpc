package com.example.zeebedemo.api;

import com.example.zeebedemo.ZeebeDemoResponseProcessor;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class ApiController {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;
    @Autowired
    private ZeebeDemoResponseProcessor demoResponseProcessor;

    @Value("${demoKey}")
    private String demoKey;

    @GetMapping("/start")
    public void start() {
        Map<String,Object> variableMap = new HashMap<>();
        variableMap.put("name","123");
        System.out.println(demoKey);
//        int randomInt = (int) ((Math.random() * (999999 - 100000)) + 100000);
        variableMap.put("demoKey", demoKey);
        ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("HelloProcess")
                .latestVersion()
                .variables(variableMap)
                .send()
                .join();
        long processInstanceKey = processInstanceEvent.getProcessInstanceKey();
        LOG.info("Workflow instance created. Key: " + processInstanceKey);
    }

    @GetMapping("/response")
    public void response() {
        demoResponseProcessor.response();
    }

    @GetMapping("/deploy")
    public void deploy() {
        DeploymentEvent deployment = zeebeClient.newDeployCommand()
                .addResourceFromClasspath("hello-process.bpmn")
                .send()
                .join();

        int version = deployment.getProcesses().get(0).getVersion();

        LOG.info("Workflow deployed. Version: {}", version);
    }
}
