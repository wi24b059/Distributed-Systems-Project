package com.energy.restapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentEnergyDto {
    private String hour;

    @JsonProperty("community_depleted")
    private double communityDepleted;

    @JsonProperty("grid_portion")
    private double gridPortion;

    public CurrentEnergyDto() {
    }

    public CurrentEnergyDto(String hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
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
