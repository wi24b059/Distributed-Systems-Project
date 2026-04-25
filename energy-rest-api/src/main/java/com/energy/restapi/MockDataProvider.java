package com.energy.restapi;

import java.util.List;

public class MockDataProvider {

    public static final List<EnergyData> CURRENT_PERCENTAGE = List.of(
            new EnergyData("2025-01-10T14:00:00", 100.00, 5.63)
    );

    public static final List<EnergyUsageData> HISTORICAL_USAGE = List.of(
            new EnergyUsageData("2025-01-10T00:00:00",  2.100,  1.800,  0.300),
            new EnergyUsageData("2025-01-10T01:00:00",  1.800,  1.500,  0.300),
            new EnergyUsageData("2025-01-10T02:00:00",  1.600,  1.400,  0.200),
            new EnergyUsageData("2025-01-10T03:00:00",  1.500,  1.300,  0.200),
            new EnergyUsageData("2025-01-10T04:00:00",  1.700,  1.500,  0.200),
            new EnergyUsageData("2025-01-10T05:00:00",  3.200,  2.900,  0.300),
            new EnergyUsageData("2025-01-10T06:00:00",  5.500,  5.100,  0.400),
            new EnergyUsageData("2025-01-10T07:00:00",  8.300,  7.800,  0.500),
            new EnergyUsageData("2025-01-10T08:00:00", 12.100, 11.500,  0.700),
            new EnergyUsageData("2025-01-10T09:00:00", 15.050, 14.033,  1.049),
            new EnergyUsageData("2025-01-10T10:00:00", 16.400, 15.200,  1.200),
            new EnergyUsageData("2025-01-10T11:00:00", 17.800, 16.900,  1.050),
            new EnergyUsageData("2025-01-10T12:00:00", 18.500, 17.750,  0.900),
            new EnergyUsageData("2025-01-10T13:00:00", 15.015, 14.033,  2.049),
            new EnergyUsageData("2025-01-10T14:00:00", 18.050, 18.050,  1.076),
            new EnergyUsageData("2025-01-10T15:00:00", 17.200, 17.200,  1.100),
            new EnergyUsageData("2025-01-10T16:00:00", 14.300, 14.100,  1.800),
            new EnergyUsageData("2025-01-10T17:00:00", 10.500, 10.200,  2.100),
            new EnergyUsageData("2025-01-10T18:00:00",  7.800,  7.600,  2.500),
            new EnergyUsageData("2025-01-10T19:00:00",  5.300,  5.100,  2.300),
            new EnergyUsageData("2025-01-10T20:00:00",  4.100,  3.900,  1.900),
            new EnergyUsageData("2025-01-10T21:00:00",  3.500,  3.200,  1.500),
            new EnergyUsageData("2025-01-10T22:00:00",  2.800,  2.500,  0.800),
            new EnergyUsageData("2025-01-10T23:00:00",  2.300,  2.000,  0.500)
    );
}

