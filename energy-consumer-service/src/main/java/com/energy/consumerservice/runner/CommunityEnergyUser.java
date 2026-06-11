package com.energy.consumerservice.runner;

import com.energy.consumerservice.dto.EnergyMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class CommunityEnergyUser implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public CommunityEnergyUser(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Community Energy User gestartet. Generiere Verbrauchs-Daten...");

        while (true) {
            Thread.sleep(1000 + random.nextInt(4001));
            double kwh = calculateUsageBasedOnTime();
            sendEnergyMessage(kwh);
        }
    }

    private double calculateUsageBasedOnTime() {
        int hour = LocalTime.now().getHour();
        double baseUsage = 0.001; 

        if ((hour >= 7 && hour <= 9) || (hour >= 18 && hour <= 21)) {
            baseUsage += 0.002;
        }

        return baseUsage + (random.nextDouble() * 0.001);
    }

    private void sendEnergyMessage(double kwh) throws Exception {
        EnergyMessageDto dto = new EnergyMessageDto();
        dto.setType("USER");
        dto.setAssociation("COMMUNITY");
        dto.setKwh(kwh);
        dto.setDatetime(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        String jsonPayload = objectMapper.writeValueAsString(dto);
        rabbitTemplate.convertAndSend("community-energy-events-queue", jsonPayload);

        System.out.println(
                "Type: " + dto.getType()
                        + ", Association: " + dto.getAssociation()
                        + ", kWh: " + dto.getKwh()
                        + ", Datetime: " + dto.getDatetime()
        );
    }
}
