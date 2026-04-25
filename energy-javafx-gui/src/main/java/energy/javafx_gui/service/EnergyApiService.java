package energy.javafx_gui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import energy.javafx_gui.dto.CurrentEnergyDto;
import energy.javafx_gui.dto.HistoricalEnergyDto;

import java.util.List;

public class EnergyApiService {
    private static final String BASE_URL = "http://localhost:9090/energy";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CurrentEnergyDto getCurrentEnergyData() throws Exception {

        return null;
    }

    public List<HistoricalEnergyDto> getHistoricalEnergyData(String start, String end) throws Exception {

        return List.of();
    }
}
