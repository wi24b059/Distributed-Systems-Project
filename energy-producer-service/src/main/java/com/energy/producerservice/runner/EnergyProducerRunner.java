
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

@Component
public class EnergyProducerRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final HttpClient httpClient;
    private final Random random = new Random();

    public EnergyProducerRunner(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // Standard Java 11 HTTP Client
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Energy Producer Service started. Generating production data...");

        while (true) {
            // Fulfills the strict "random 1-5 second intervals" requirement
            int sleepInterval = 1000 + random.nextInt(4001);
            Thread.sleep(sleepInterval);

            double kwh = 0.002;

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=48.2082&longitude=16.3738&current_weather=true"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.body().contains("\"is_day\":1")) {
                    kwh += 0.005; // Boost during daytime
                }
                kwh += (random.nextDouble() * 0.001);
            } catch (Exception e) {
                kwh = 0.003; // Fallback on network error
            }

            // Populate the DTO as requested
            EnergyMessageDto dto = new EnergyMessageDto();
            dto.setType("PRODUCER");
            dto.setAssociation("COMMUNITY");
            dto.setKwh(kwh);
            dto.setDatetime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Format JSON purely via standard Java String.format
            // Locale.US guarantees a dot (.) is used for decimals, preventing JSON syntax errors
            String jsonPayload = String.format(Locale.US,
                    "{\"type\":\"%s\",\"association\":\"%s\",\"kwh\":%f,\"datetime\":\"%s\"}",
                    dto.getType(), dto.getAssociation(), dto.getKwh(), dto.getDatetime()
            );

            rabbitTemplate.convertAndSend("community-energy-events-queue", jsonPayload);
            System.out.println("Produced: " + String.format(Locale.US, "%.5f", dto.getKwh()) + " kWh");
        }
    }
}