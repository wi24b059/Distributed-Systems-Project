package com.energy.percentageservice.service;

import com.energy.percentageservice.dto.UsageDataUpdatedDto;
import com.energy.percentageservice.model.CurrentPercentageEntity;
import com.energy.percentageservice.repository.CurrentPercentageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CurrentPercentageProcessingService {

    private final CurrentPercentageRepository currentPercentageRepository;

    public CurrentPercentageProcessingService(
            CurrentPercentageRepository currentPercentageRepository
    ) {
        this.currentPercentageRepository = currentPercentageRepository;
    }

    public void processUsageDataUpdatedMessage(UsageDataUpdatedDto message) {
        LocalDateTime usageHour = LocalDateTime.parse(message.getUsageHour());

        double communityDepleted = calculateCommunityDepleted(
                message.getCommunityProduced(),
                message.getCommunityUsed()
        );

        double gridPortion = calculateGridPortion(
                message.getCommunityUsed(),
                message.getGridUsed()
        );

        CurrentPercentageEntity currentPercentageEntity = new CurrentPercentageEntity();
        currentPercentageEntity.setUsageHour(usageHour);
        currentPercentageEntity.setCommunityDepleted(communityDepleted);
        currentPercentageEntity.setGridPortion(gridPortion);

        currentPercentageRepository.save(currentPercentageEntity);

        System.out.println("Saved current percentage:");
        System.out.println("Hour: " + currentPercentageEntity.getUsageHour());
        System.out.println("Community depleted: " + currentPercentageEntity.getCommunityDepleted());
        System.out.println("Grid portion: " + currentPercentageEntity.getGridPortion());
    }

    private double calculateCommunityDepleted(double communityProduced, double communityUsed) {
        if (communityProduced <= 0) {
            return 0.0;
        }

        double percentage = (communityUsed / communityProduced) * 100.0;
        return Math.min(percentage, 100.0);
    }

    private double calculateGridPortion(double communityUsed, double gridUsed) {
        double totalUsed = communityUsed + gridUsed;

        if (totalUsed <= 0) {
            return 0.0;
        }

        return (gridUsed / totalUsed) * 100.0;
    }
}