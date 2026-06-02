package com.energy.producerservice.scheduler;

import com.energy.producerservice.dto.EnergyMessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EnergyProducerTask {

    private final RabbitTemplate rabbitTemplate;
    private final HttpClient httpClient;

    public EnergyProducerTask(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Scheduled(fixedRate = 4000)
    public void produceEnergy() {
        double kwh = 0.002;

        try {
            // Standard Java 11 HTTP Client (No RestTemplate required)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=48.2082&longitude=16.3738&current_weather=true"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Clean string check instead of complex JSON tree parsing
            if (response.body().contains("\"is_day\":1")) {
                kwh += 0.005; // Boost during daytime
            }
            kwh += (Math.random() * 0.001);
        } catch (Exception e) {
            kwh = 0.003; // Fallback on error
        }

        // 1. Populate the DTO
        EnergyMessageDto dto = new EnergyMessageDto();
        dto.setType("PRODUCER");
        dto.setAssociation("COMMUNITY");
        dto.setKwh(kwh);
        dto.setDatetime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // 2. Format JSON directly from the DTO (No ObjectMapper required)
        String jsonPayload = String.format(
                "{\"type\":\"%s\",\"association\":\"%s\",\"kwh\":%s,\"datetime\":\"%s\"}",
                dto.getType(), dto.getAssociation(), dto.getKwh(), dto.getDatetime()
        );

        // 3. Send to queue
        rabbitTemplate.convertAndSend("community-energy-events-queue", jsonPayload);
        System.out.println("Produced: " + dto.getKwh() + " kWh");
    }
}