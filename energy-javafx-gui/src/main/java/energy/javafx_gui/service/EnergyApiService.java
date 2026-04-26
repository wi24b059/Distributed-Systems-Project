package energy.javafx_gui.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import energy.javafx_gui.dto.CurrentEnergyDto;
import energy.javafx_gui.dto.HistoricalEnergyDto;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EnergyApiService {

    private static final String BASE_URL = "http://localhost:9090/energy";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CurrentEnergyDto getCurrentEnergyData() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/current"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), CurrentEnergyDto.class);
    }

    public List<HistoricalEnergyDto> getHistoricalEnergyData(String start, String end) throws Exception {
        String url = BASE_URL + "/historical?start="
                + URLEncoder.encode(start, StandardCharsets.UTF_8)
                + "&end="
                + URLEncoder.encode(end, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: " + response.statusCode());
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<HistoricalEnergyDto>>() {}
        );
    }
}