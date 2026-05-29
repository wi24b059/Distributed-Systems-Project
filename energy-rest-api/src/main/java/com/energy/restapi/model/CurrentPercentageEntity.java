package com.energy.restapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentageEntity {

    @Id
    @Column(name = "usage_hour", nullable = false)
    private LocalDateTime usageHour;

    @Column(name = "community_depleted", nullable = false)
    private double communityDepleted;

    @Column(name = "grid_portion", nullable = false)
    private double gridPortion;

    public LocalDateTime getUsageHour() {
        return usageHour;
    }

    public void setUsageHour(LocalDateTime usageHour) {
        this.usageHour = usageHour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}