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

/*
This class is responsible for initiating and managing the REST API calls needed to run the bpmn flow.
All endpoints are accessible through port 8081 of localhost. This can be changed in the application.properties file.
*/

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
    /*Calling the start method will start the bpmn flow.
    First a variable map is created, to which the initial variables are added.
    These initial variables are a name (in this demo this is 123), and a demoKey, which is a randomly generated number,
    that we shall use as our correlation key in the future. We print out this demoKey, so we can compare it to the
    correlation key later, for ease of understanding.

    Then we create a new instance of the bpmn process "HelloProcess", and send it the variable map we just created.
    From here on the workflow has started and workers will handle the tasks in the bpmn flow.

    We also print out the workflow instance key. You can use this to identify the kafka messages related to a
    specific instance of a workflow.

    For more details on the Zeebe workers, please see the WorkerManager class.
    */
    public void start() {
        Map<String,Object> variableMap = new HashMap<>();
        variableMap.put("name","123");
        System.out.println(demoKey);
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
    /*
    Calling the response method, will result in the response task in the bpmn (HelloResponse),
    receiving an appropriate response. After this the bpmn flow will continue, with Zeebe workers handling
    the necessary tasks. For more details, please see ZeebeDemoResponseProcessor class.

    If the method isn't called, the bpmn will circle back to it's first worker after a timeout of 1 minute.
    This will happen indefinitely.
    */
    public void response() {
        demoResponseProcessor.response();
    }

    @GetMapping("/deploy")
    /*
    Calling the deploy method, will deploy a new version of your bpmn. It is necessary to do this every time
    you make an alteration in the bpmn.

    After calling deploy, please remember to also call the start method, since deploying a bpmn does not create
    an instance of it.
    */
    public void deploy() {
        DeploymentEvent deployment = zeebeClient.newDeployCommand()
                .addResourceFromClasspath("hello-process.bpmn")
                .send()
                .join();

        int version = deployment.getProcesses().get(0).getVersion();

        LOG.info("Workflow deployed. Version: {}", version);
    }
}
