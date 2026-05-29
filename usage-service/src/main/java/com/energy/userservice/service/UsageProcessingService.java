package com.energy.userservice.service;

import com.energy.userservice.dto.EnergyMessageDto;
import com.energy.userservice.dto.UsageDataUpdatedDto;
import com.energy.userservice.model.UsageDataEntity;
import com.energy.userservice.repository.UsageDataRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageProcessingService {

    private final UsageDataRepository usageDataRepository;
    private final RabbitTemplate rabbitTemplate;

    public UsageProcessingService(
            UsageDataRepository usageDataRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.usageDataRepository = usageDataRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void processEnergyMessage(EnergyMessageDto message) {
        if (!"COMMUNITY".equalsIgnoreCase(message.getAssociation())) {
            return;
        }

        LocalDateTime usageHour = LocalDateTime.parse(message.getDatetime())
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        UsageDataEntity usageDataEntity = usageDataRepository
                .findById(usageHour)
                .orElseGet(() -> createEmptyUsageDataEntity(usageHour));

        if ("PRODUCER".equalsIgnoreCase(message.getType())) {
            double updatedProduced = usageDataEntity.getCommunityProduced() + message.getKwh();
            usageDataEntity.setCommunityProduced(updatedProduced);

        } else if ("USER".equalsIgnoreCase(message.getType())) {
            double requestedEnergy = message.getKwh();

            double availableCommunityEnergy =
                    usageDataEntity.getCommunityProduced() - usageDataEntity.getCommunityUsed();

            if (availableCommunityEnergy < 0) {
                availableCommunityEnergy = 0;
            }

            double communityPart = Math.min(requestedEnergy, availableCommunityEnergy);
            double gridPart = requestedEnergy - communityPart;

            usageDataEntity.setCommunityUsed(
                    usageDataEntity.getCommunityUsed() + communityPart
            );

            usageDataEntity.setGridUsed(
                    usageDataEntity.getGridUsed() + gridPart
            );
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getType());
        }

        UsageDataEntity savedUsageData = usageDataRepository.save(usageDataEntity);

        UsageDataUpdatedDto updateMessage = new UsageDataUpdatedDto(
                savedUsageData.getHour().toString(),
                savedUsageData.getCommunityProduced(),
                savedUsageData.getCommunityUsed(),
                savedUsageData.getGridUsed()
        );

        rabbitTemplate.convertAndSend("usage-data-updated-queue", updateMessage);

        System.out.println("Saved usage data:");
        System.out.println("Hour: " + savedUsageData.getHour());
        System.out.println("Produced: " + savedUsageData.getCommunityProduced());
        System.out.println("Used: " + savedUsageData.getCommunityUsed());
        System.out.println("Grid used: " + savedUsageData.getGridUsed());
    }

    private UsageDataEntity createEmptyUsageDataEntity(LocalDateTime usageHour) {
        UsageDataEntity usageDataEntity = new UsageDataEntity();
        usageDataEntity.setHour(usageHour);
        usageDataEntity.setCommunityProduced(0.0);
        usageDataEntity.setCommunityUsed(0.0);
        usageDataEntity.setGridUsed(0.0);
        return usageDataEntity;
    }
}