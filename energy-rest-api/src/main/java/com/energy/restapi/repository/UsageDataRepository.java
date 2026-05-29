package com.energy.restapi.repository;

import com.energy.restapi.model.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {

    List<UsageDataEntity> findByUsageHourBetweenOrderByUsageHourAsc(
            LocalDateTime start,
            LocalDateTime end
    );
}