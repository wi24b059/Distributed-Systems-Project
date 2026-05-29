package com.energy.userservice.messaging;

import com.energy.userservice.dto.EnergyMessageDto;
import com.energy.userservice.service.UsageProcessingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EnergyMessageListener {

    private final UsageProcessingService usageProcessingService;

    public EnergyMessageListener(UsageProcessingService usageProcessingService) {
        this.usageProcessingService = usageProcessingService;
    }

    @RabbitListener(queues = "community-energy-events-queue")
    public void readEnergyMessage(EnergyMessageDto message) {
        System.out.println("Received energy message:");
        System.out.println(message.getType() + " | " + message.getAssociation() + " | " + message.getKwh() + " | " + message.getDatetime());

        usageProcessingService.processEnergyMessage(message);
    }
}