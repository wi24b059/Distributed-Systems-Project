package com.energy.usageservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsageServiceApplication {

	@Bean
	public Queue communityEnergyEventsQueue() {
		return new Queue("community-energy-events-queue", true);
	}

	@Bean
	public Queue usageDataUpdatedQueue() {
		return new Queue("usage-data-updated-queue", true);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(UsageServiceApplication.class, args);
	}
}