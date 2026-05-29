package com.energy.userservice.repository;

import com.energy.userservice.model.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {
}