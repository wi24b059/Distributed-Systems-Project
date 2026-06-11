package com.energy.percentageservice.messaging;

import com.energy.percentageservice.dto.UsageDataUpdatedDto;
import com.energy.percentageservice.service.CurrentPercentageProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class UsageDataUpdatedListener {

    private final CurrentPercentageProcessingService currentPercentageProcessingService;
    private final ObjectMapper objectMapper;

    public UsageDataUpdatedListener(
            CurrentPercentageProcessingService currentPercentageProcessingService,
            ObjectMapper objectMapper
    ) {
        this.currentPercentageProcessingService = currentPercentageProcessingService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "usage-data-updated-queue")
    public void readUsageDataUpdatedMessage(String messageJson) {
        try {
            UsageDataUpdatedDto message =
                    objectMapper.readValue(messageJson, UsageDataUpdatedDto.class);

            System.out.println("Received usage update message:");
            System.out.println(
                    message.getUsageHour() + " | "
                            + message.getCommunityProduced() + " | "
                            + message.getCommunityUsed() + " | "
                            + message.getGridUsed()
            );

            currentPercentageProcessingService.processUsageDataUpdatedMessage(message);

        } catch (Exception exception) {
            System.out.println("Percentage listener error: " + exception.getMessage());
        }
    }
}