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

/**
 * This class is responsible for initiating and managing the REST API endpoints needed to run the bpmn flow.
 * All endpoints are accessible through the port configured in application.properties.
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

    /**
     * Calling the /start endpoint will start the bpmn flow.
     * First a variable map is created, to which the initial variables are added.
     * These initial variables are a name (in this demo this is 123), and a demoKey, which is a randomly generated number,
     * that we shall use as our correlation key in the future. We print out this demoKey, so we can compare it to the
     * correlation key later, for ease of understanding.
     * <p>
     * Then we create a new instance of the bpmn process "HelloProcess", and send it the variable map we just created.
     * From here on the workflow has started and workers will handle the tasks in the bpmn flow.
     * <p>
     * We also print out the workflow instance key. You can use this to identify the kafka messages related to a
     * specific instance of a workflow.
     */

    @GetMapping("/start")
    public void start() {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("name", "123");
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

    /**
     * Calling the /response endpoint, will result in the response task in the bpmn (HelloResponse),
     * receiving an appropriate response. After this the bpmn flow will continue, with Zeebe workers handling
     * the necessary tasks.
     * <p>
     * If the method isn't called, the bpmn will circle back to it's first worker after a timeout of 1 minute.
     * This will happen indefinitely.
     */
    @GetMapping("/response")
    public void response() {
        demoResponseProcessor.response();
    }

    /**
     * Calling the /deploy endpoint, will deploy a new version of your bpmn.
     */
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
