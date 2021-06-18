package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ZeebeClientFactory {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    //	TODO: @Value
    private String gatewayAddress = "127.0.0.1:26500";

    @Bean
    public ZeebeClient zeebeClient(){
        LOG.info("Zeebe client connecting to {}", gatewayAddress);
        return ZeebeClient.newClientBuilder()
                .gatewayAddress(gatewayAddress)
                .usePlaintext()
                .build();
    }
}
