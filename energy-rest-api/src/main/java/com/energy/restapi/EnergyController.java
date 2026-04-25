package com.energy.restapi;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    /**
     * Returns the percentage data for the current hour (mock data).
     * GET /energy/current
     */
    @GetMapping("/current")
    public ResponseEntity<EnergyData> getCurrentHour() {
        return ResponseEntity.ok(MockDataProvider.CURRENT_PERCENTAGE.get(0));
    }

    /**
     * Returns historical usage data, optionally filtered by a time range.
     * GET /energy/historical?start=2025-01-10T06:00:00&end=2025-01-10T14:00:00
     *
     * @param start (optional) inclusive start of the time range (ISO-8601, e.g. 2025-01-10T06:00:00)
     * @param end   (optional) inclusive end   of the time range (ISO-8601, e.g. 2025-01-10T14:00:00)
     */
    @GetMapping("/historical")
    public ResponseEntity<List<EnergyUsageData>> getHistoricalData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<EnergyUsageData> result = MockDataProvider.HISTORICAL_USAGE.stream()
                .filter(entry -> {
                    LocalDateTime entryTime = LocalDateTime.parse(entry.hour());
                    boolean afterStart = (start == null) || !entryTime.isBefore(start);
                    boolean beforeEnd  = (end   == null) || !entryTime.isAfter(end);
                    return afterStart && beforeEnd;
                })
                .toList();

        return ResponseEntity.ok(result);
    }
}

