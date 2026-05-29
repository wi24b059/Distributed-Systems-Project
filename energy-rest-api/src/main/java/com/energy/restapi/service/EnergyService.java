package com.energy.restapi.service;

import com.energy.restapi.dto.CurrentEnergyDto;
import com.energy.restapi.dto.HistoricalEnergyDto;
import com.energy.restapi.model.CurrentPercentageEntity;
import com.energy.restapi.model.UsageDataEntity;
import com.energy.restapi.repository.CurrentPercentageRepository;
import com.energy.restapi.repository.UsageDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EnergyService {

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final CurrentPercentageRepository currentPercentageRepository;
    private final UsageDataRepository usageDataRepository;

    public EnergyService(
            CurrentPercentageRepository currentPercentageRepository,
            UsageDataRepository usageDataRepository
    ) {
        this.currentPercentageRepository = currentPercentageRepository;
        this.usageDataRepository = usageDataRepository;
    }

    public CurrentEnergyDto getCurrentEnergyData() {
        CurrentPercentageEntity currentPercentageEntity = currentPercentageRepository
                .findTopByOrderByUsageHourDesc()
                .orElseThrow(() -> new RuntimeException("No current percentage data found."));

        return mapToCurrentEnergyDto(currentPercentageEntity);
    }

    public List<HistoricalEnergyDto> getHistoricalEnergyData(String start, String end) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);

        if (startDateTime.isAfter(endDateTime)) {
            throw new RuntimeException("Start datetime must be before or equal to end datetime.");
        }

        List<UsageDataEntity> usageDataEntities = usageDataRepository
                .findByUsageHourBetweenOrderByUsageHourAsc(startDateTime, endDateTime);

        if (usageDataEntities.isEmpty()) {
            throw new RuntimeException("No historical energy data found for the selected time range.");
        }

        return usageDataEntities.stream()
                .map(this::mapToHistoricalEnergyDto)
                .toList();
    }

    private CurrentEnergyDto mapToCurrentEnergyDto(CurrentPercentageEntity entity) {
        return new CurrentEnergyDto(
                entity.getUsageHour().format(DISPLAY_FORMATTER),
                entity.getCommunityDepleted(),
                entity.getGridPortion()
        );
    }

    private HistoricalEnergyDto mapToHistoricalEnergyDto(UsageDataEntity entity) {
        return new HistoricalEnergyDto(
                entity.getUsageHour().format(DISPLAY_FORMATTER),
                entity.getCommunityProduced(),
                entity.getCommunityUsed(),
                entity.getGridUsed()
        );
    }
}