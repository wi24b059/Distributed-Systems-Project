package com.energy.usageservice.service;

import com.energy.usageservice.dto.EnergyMessageDto;
import com.energy.usageservice.dto.UsageDataUpdatedDto;
import com.energy.usageservice.model.UsageDataEntity;
import com.energy.usageservice.repository.UsageDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageProcessingService {

    private final UsageDataRepository usageDataRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UsageProcessingService(UsageDataRepository usageDataRepository,
                                  RabbitTemplate rabbitTemplate,
                                  ObjectMapper objectMapper) {
        this.usageDataRepository = usageDataRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void processEnergyMessage(EnergyMessageDto message) {
        LocalDateTime messageDateTime = LocalDateTime.parse(message.getDatetime());
        LocalDateTime usageHour = messageDateTime
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        UsageDataEntity usageDataEntity = usageDataRepository.findById(usageHour)
                .orElseGet(() -> {
                    UsageDataEntity newEntity = new UsageDataEntity();
                    newEntity.setHour(usageHour);
                    newEntity.setCommunityProduced(0.0);
                    newEntity.setCommunityUsed(0.0);
                    newEntity.setGridUsed(0.0);
                    return newEntity;
                });

        if ("PRODUCER".equals(message.getType())) {
            usageDataEntity.setCommunityProduced(
                    usageDataEntity.getCommunityProduced() + message.getKwh()
            );
        }

        if ("USER".equals(message.getType())) {
            double availableCommunityEnergy =
                    usageDataEntity.getCommunityProduced() - usageDataEntity.getCommunityUsed();

            if (availableCommunityEnergy < 0) {
                availableCommunityEnergy = 0;
            }

            double communityPart = Math.min(message.getKwh(), availableCommunityEnergy);
            double gridPart = message.getKwh() - communityPart;

            usageDataEntity.setCommunityUsed(
                    usageDataEntity.getCommunityUsed() + communityPart
            );

            usageDataEntity.setGridUsed(
                    usageDataEntity.getGridUsed() + gridPart
            );
        }

        usageDataRepository.save(usageDataEntity);

        System.out.println("Saved usage data:");
        System.out.println("Hour: " + usageDataEntity.getHour());
        System.out.println("Produced: " + usageDataEntity.getCommunityProduced());
        System.out.println("Used: " + usageDataEntity.getCommunityUsed());
        System.out.println("Grid used: " + usageDataEntity.getGridUsed());

        sendUsageUpdateMessage(usageDataEntity);
    }

    private void sendUsageUpdateMessage(UsageDataEntity usageDataEntity) {
        try {
            UsageDataUpdatedDto usageDataUpdatedDto = new UsageDataUpdatedDto(
                    usageDataEntity.getHour().toString(),
                    usageDataEntity.getCommunityProduced(),
                    usageDataEntity.getCommunityUsed(),
                    usageDataEntity.getGridUsed()
            );

            String jsonMessage = objectMapper.writeValueAsString(usageDataUpdatedDto);

            rabbitTemplate.convertAndSend("usage-data-updated-queue", jsonMessage);

        } catch (Exception exception) {
            System.out.println("Usage update send error: " + exception.getMessage());
        }
    }
}