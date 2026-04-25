package com.energy.restapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnergyData(
        String hour,
        @JsonProperty("community_depleted") double communityDepleted,
        @JsonProperty("grid_portion") double gridPortion
) {}

