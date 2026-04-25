package com.energy.restapi.controller;

import com.energy.restapi.dto.CurrentEnergyDto;
import com.energy.restapi.dto.HistoricalEnergyDto;
import com.energy.restapi.service.EnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private EnergyService energyService;

    /**
     * Returns the percentage data for the current hour.
     * GET /energy/current
     *
     * @return example percentage data of the current hour
     */
    @GetMapping("/current")
    public ResponseEntity<CurrentEnergyDto> getCurrentHour() {
        CurrentEnergyDto currentEnergyDto = energyService.getCurrentEnergyData();
        return ResponseEntity.ok(currentEnergyDto);
    }

    /**
     * Returns example historical energy data for the selected time period.
     * GET /energy/historical?start=...&end=...
     *
     * @param start start datetime in ISO format, e.g. 2025-01-10T06:00:00
     * @param end end datetime in ISO format, e.g. 2025-01-10T14:00:00
     * @return filtered historical example data
     */
    @GetMapping("/historical")
    public ResponseEntity<List<HistoricalEnergyDto>> getHistoricalEnergy(
            @RequestParam String start,
            @RequestParam String end) {

        List<HistoricalEnergyDto> historicalEnergyData =
                energyService.getHistoricalEnergyData(start, end);

        return ResponseEntity.ok(historicalEnergyData);
    }

    @Autowired
    public void setEnergyService(EnergyService energyService) {
        this.energyService = energyService;
    }
}

