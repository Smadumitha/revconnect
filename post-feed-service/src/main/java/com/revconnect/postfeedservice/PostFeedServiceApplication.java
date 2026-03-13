package com.revconnect.postfeedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class PostFeedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostFeedServiceApplication.class, args);
    }
}

