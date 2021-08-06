package com.example.zeebedemo.api;

import com.example.zeebedemo.ZeebeDemoResponseProcessor;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiController {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

    @GetMapping("/start")
    public void start() {
        Map<String,Object> variableMap = new HashMap<>();
        variableMap.put("Name","Joe");
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
        ZeebeDemoResponseProcessor demoResponseProcessor = new ZeebeDemoResponseProcessor();
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
