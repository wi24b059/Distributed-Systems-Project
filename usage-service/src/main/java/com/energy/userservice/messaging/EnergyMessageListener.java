package com.energy.userservice.messaging;

import com.energy.userservice.dto.EnergyMessageDto;
import com.energy.userservice.service.UsageProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EnergyMessageListener {

    private final UsageProcessingService usageProcessingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EnergyMessageListener(UsageProcessingService usageProcessingService) {
        this.usageProcessingService = usageProcessingService;
    }

    @RabbitListener(queues = "community-energy-events-queue")
    public void readEnergyMessage(String message) {
        try {
            EnergyMessageDto energyMessageDto =
                    objectMapper.readValue(message, EnergyMessageDto.class);

            System.out.println("Received energy message:");
            System.out.println(
                    energyMessageDto.getType() + " | "
                            + energyMessageDto.getAssociation() + " | "
                            + energyMessageDto.getKwh() + " | "
                            + energyMessageDto.getDatetime()
            );

            usageProcessingService.processEnergyMessage(energyMessageDto);

        } catch (Exception exception) {
            System.out.println("Usage listener error: " + exception.getMessage());
        }
    }
}