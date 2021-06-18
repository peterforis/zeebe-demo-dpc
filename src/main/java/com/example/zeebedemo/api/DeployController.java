package com.example.zeebedemo.api;

import com.example.zeebedemo.ZeebeDemoApplication;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;

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
