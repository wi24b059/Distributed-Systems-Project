package com.energy.usageservice.repository;

import com.energy.usageservice.model.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {
}