package com.energy.restapi.repository;

import com.energy.restapi.model.CurrentPercentageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentageEntity, LocalDateTime> {

    Optional<CurrentPercentageEntity> findTopByOrderByUsageHourDesc();
}