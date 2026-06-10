package com.energy.usageservice.dto;

public class UsageDataUpdatedDto {

    private String usageHour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public UsageDataUpdatedDto() {
    }

    public UsageDataUpdatedDto(String usageHour, double communityProduced, double communityUsed, double gridUsed) {
        this.usageHour = usageHour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getUsageHour() {
        return usageHour;
    }

    public void setUsageHour(String usageHour) {
        this.usageHour = usageHour;
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