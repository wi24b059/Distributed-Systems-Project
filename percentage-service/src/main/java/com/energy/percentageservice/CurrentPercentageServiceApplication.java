package com.energy.percentageservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CurrentPercentageServiceApplication {

    @Bean
    public Queue usageDataUpdatedQueue() {
        return new Queue("usage-data-updated-queue", true);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(CurrentPercentageServiceApplication.class, args);
    }
}