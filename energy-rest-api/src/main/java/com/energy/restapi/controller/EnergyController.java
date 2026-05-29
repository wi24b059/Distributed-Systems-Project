package com.energy.restapi.controller;

import com.energy.restapi.dto.CurrentEnergyDto;
import com.energy.restapi.dto.HistoricalEnergyDto;
import com.energy.restapi.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentEnergyDto> getCurrentHour() {
        CurrentEnergyDto currentEnergyDto = energyService.getCurrentEnergyData();
        return ResponseEntity.ok(currentEnergyDto);
    }

    @GetMapping("/historical")
    public ResponseEntity<List<HistoricalEnergyDto>> getHistoricalEnergy(
            @RequestParam String start,
            @RequestParam String end
    ) {
        List<HistoricalEnergyDto> historicalEnergyData =
                energyService.getHistoricalEnergyData(start, end);

        return ResponseEntity.ok(historicalEnergyData);
    }
}