package com.energy.producerservice.runner;

import com.energy.producerservice.dto.EnergyMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommunityEnergyProducer implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Random random = new Random();

    public CommunityEnergyProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Community Energy Producer gestartet. Generiere Wetter-Daten...");
        Pattern weatherCodePattern = Pattern.compile("\"weathercode\"\\s*:\\s*(\\d+)");

        while (true) {
            Thread.sleep(1000 + random.nextInt(4001));
            double kwh = 0.002;

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=48.2082&longitude=16.3738&current_weather=true"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.body().contains("\"is_day\":1")) {
                    kwh += 0.001; 
                    Matcher matcher = weatherCodePattern.matcher(response.body());
                    
                    if (matcher.find()) {
                        int weatherCode = Integer.parseInt(matcher.group(1));
                        if (weatherCode == 0 || weatherCode == 1) { 
                            kwh += 0.004;
                        } else if (weatherCode == 2) { 
                            kwh += 0.002;
                        }
                    }
                }
                kwh += (random.nextDouble() * 0.001); 

            } catch (Exception e) {
                kwh = 0.003; 
            }

            sendEnergyMessage(kwh);
        }
    }

    private void sendEnergyMessage(double kwh) throws Exception {
        EnergyMessageDto dto = new EnergyMessageDto();
        dto.setType("PRODUCER");
        dto.setAssociation("COMMUNITY");
        dto.setKwh(kwh);
        dto.setDatetime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        String jsonPayload = objectMapper.writeValueAsString(dto);
        rabbitTemplate.convertAndSend("community-energy-events-queue", jsonPayload);

        System.out.printf(Locale.US, "Produced: %.5f kWh%n", dto.getKwh());
    }
}
