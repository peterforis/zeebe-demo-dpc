package com.example.zeebedemo;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class ZeebeClientFactory {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${gatewayAddress}")
    private String gatewayAddress;

    @Bean
    public ZeebeClient zeebeClient(){
        LOG.info("Zeebe client connecting to {}", gatewayAddress);
        return ZeebeClient.newClientBuilder()
                .gatewayAddress(gatewayAddress)
                .usePlaintext()
                .build();
    }
}
