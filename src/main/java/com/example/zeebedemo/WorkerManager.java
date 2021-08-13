package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * This class is responsible for describing various Zeebe workers, for the tasks within the hello-process bpmn.
 */


@Component
public class WorkerManager {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZeebeClient zeebeClient;


    @PostConstruct
    public void setup() {

        setupHelloWorldWorker();

        setupHelloDefaultWorker();

        setupHelloConditionWorker();

        setupHelloChecker();
    }

    /**
     * This worker is responsible for handling all jobs, with the type hello-checker. We only have one of these, the
     * service task HelloChecker.
     * The worker logs the message "Succesfully recieved response, so that we can verify, that the receive task
     * HelloResponse received the correct response, and we have moved on with the workflow.
     */
    private JobWorker setupHelloChecker() {
        return zeebeClient.newWorker().jobType("hello-checker").handler((client, job) -> {
            Map<String, Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Successfully received response");
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();
    }

    /**
     * This worker is responsible for handling all jobs, with the type hello-condition. We only have one of these, the
     * service task HelloCondition.
     * The worker first collects the variables currently associated with the workflow as a map. It then retrieves
     * the value corresponding to the key "hellonumber", and prints it out. This is done so that we may verify,
     * that the correct path was taken in the bpmn flow at the exclusive gateway.
     */
    private void setupHelloConditionWorker() {
        zeebeClient.newWorker().jobType("hello-condition").handler((client, job) -> {
            Map<String, Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Number received {}", incomingVariables.get("hellonumber"));
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();
    }

    /**
     * This worker is responsible for handling all jobs, with the type hello-default. We only have one of these, the
     * service task HelloDefault.
     * The worker first collects the variables currently associated with the workflow as a map. It then retrieves
     * the value corresponding to the key "hellonumber", and prints it out. This is done so that we may verify,
     * that the correct path was taken in the bpmn flow at the exclusive gateway.
     */
    private void setupHelloDefaultWorker() {
        zeebeClient.newWorker().jobType("hello-default").handler((client, job) -> {
            Map<String, Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Number received {}", incomingVariables.get("hellonumber"));
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();
    }

    /**
     * This worker is responsible for handling all jobs, with the type hello-world. We only have one of these, the
     * service task HelloTask.
     * The worker first collects the variables currently associated with the workflow as a map. It then retrieves
     * the value corresponding to the key "name", and prints out a greeting.
     * <p>
     * It also creates a String message using this name, and appends this to the Map. After completing the task,
     * it sends back the updated Map of variables, which now contains the message. You can see this both in the kafka
     * messages sent, and on Camunda operate.
     */
    private void setupHelloWorldWorker() {
        zeebeClient.newWorker().jobType("hello-world").handler((client, job) -> {
            Map<String, Object> incomingVariables = job.getVariablesAsMap();
            LOG.info("Hello {}", incomingVariables.get("name"));
            String message = "I am a message " + incomingVariables.get("name");
            incomingVariables.put("message", message);
            client.newCompleteCommand(job.getKey())
                    .variables(incomingVariables)
                    .send();
        }).open();
    }
}
