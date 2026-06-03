
package com.energy.consumerservice.runner;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

@Component
public class EnergyConsumerRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public EnergyConsumerRunner(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Energy Consumer Service started. Generating usage data...");

        while (true) {
            // "random 1-5 second intervals"
            int sleepInterval = 1000 + random.nextInt(4001);
            Thread.sleep(sleepInterval);

            double kwh = calculateUsageBasedOnTime();

            // "datetime: the datetime of the energy usage"
            String datetime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // "type: USER, association: COMMUNITY"
            // Using Locale.US to ensure the double uses a dot (.) instead of a comma for JSON validity
            String jsonPayload = String.format(Locale.US,
                    "{\"type\":\"USER\",\"association\":\"COMMUNITY\",\"kwh\":%f,\"datetime\":\"%s\"}",
                    kwh, datetime
            );

            rabbitTemplate.convertAndSend("community-energy-events-queue", jsonPayload);
            System.out.println("Consumed: " + String.format(Locale.US, "%.5f", kwh) + " kWh");
        }
    }

    private double calculateUsageBasedOnTime() {
        int hour = LocalTime.now().getHour();

        // "kwh: the kWh used in a minute (e.g. 0.001)"
        double baseUsage = 0.001;

        // "Incorporate the time of day to make sure more energy is needed in peak hours in the morning and in the evening."
        if ((hour >= 7 && hour <= 9) || (hour >= 18 && hour <= 21)) {
            baseUsage += 0.002;
        }

        // "semi random (but plausible) amount"
        return baseUsage + (random.nextDouble() * 0.001);
    }
}