package com.revconnect.interactionnotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InteractionNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteractionNotificationServiceApplication.class, args);
    }

}
