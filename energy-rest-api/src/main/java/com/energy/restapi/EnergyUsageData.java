package com.energy.restapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnergyUsageData(
        String hour,
        @JsonProperty("community_produced") double communityProduced,
        @JsonProperty("community_used") double communityUsed,
        @JsonProperty("grid_used") double gridUsed
) {}

