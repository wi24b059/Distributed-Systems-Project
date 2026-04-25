package com.energy.restapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistoricalEnergyDto {
    private String hour;

    @JsonProperty("community_produced")
    private double communityProduced;

    @JsonProperty("community_used")
    private double communityUsed;

    @JsonProperty("grid_used")
    private double gridUsed;

    public HistoricalEnergyDto() {
    }

    public HistoricalEnergyDto(String hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }
}
