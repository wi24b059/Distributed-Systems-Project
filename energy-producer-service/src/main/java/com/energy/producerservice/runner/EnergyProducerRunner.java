package com.energy.producerservice.runner;

import com.energy.producerservice.dto.EnergyMessageDto;
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
public class EnergyProducerRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final HttpClient httpClient;
    private final Random random = new Random();

    public EnergyProducerRunner(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Energy Producer Service started. Generating production data...");

        // Pre-compile the regex pattern for efficiency in the loop
        Pattern weatherCodePattern = Pattern.compile("\"weathercode\"\\s*:\\s*(\\d+)");

        while (true) {
            int sleepInterval = 1000 + random.nextInt(4001);
            Thread.sleep(sleepInterval);

            double kwh = 0.002; // Base minimum production

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=48.2082&longitude=16.3738&current_weather=true"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 1. Check if it is daytime
                if (response.body().contains("\"is_day\":1")) {
                    kwh += 0.001; // Small baseline boost just for ambient daylight

                    // 2. Extract actual meteorological weather code
                    Matcher matcher = weatherCodePattern.matcher(response.body());
                    if (matcher.find()) {
                        int weatherCode = Integer.parseInt(matcher.group(1));

                        // WMO Codes: 0 = Clear, 1 = Mainly clear, 2 = Partly cloudy, 3 = Overcast
                        // > 50 indicates varying degrees of rain, snow, or thunderstorms
                        if (weatherCode == 0 || weatherCode == 1) {
                            kwh += 0.004; // Maximum output for clear skies
                        } else if (weatherCode == 2) {
                            kwh += 0.002; // Reduced output for partial cloud cover
                        }
                        // If code is 3 (overcast) or higher (rain/snow), no extra solar boost is added
                    }
                }

                kwh += (random.nextDouble() * 0.001); // Random fluctuation

            } catch (Exception e) {
                kwh = 0.003; // Fallback on network error
            }

            EnergyMessageDto dto = new EnergyMessageDto();
            dto.setType("PRODUCER");
            dto.setAssociation("COMMUNITY");
            dto.setKwh(kwh);
            dto.setDatetime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            String jsonPayload = String.format(Locale.US,
                    "{\"type\":\"%s\",\"association\":\"%s\",\"kwh\":%f,\"datetime\":\"%s\"}",
                    dto.getType(), dto.getAssociation(), dto.getKwh(), dto.getDatetime()
            );

            rabbitTemplate.convertAndSend("community-energy-events-queue", jsonPayload);
            System.out.println("Produced: " + String.format(Locale.US, "%.5f", dto.getKwh()) + " kWh");
        }
    }
}