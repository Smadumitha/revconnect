package com.revconnect.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/userService")
    public Mono<String> userServiceFallback() {
        return Mono.just("User Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/postService")
    public Mono<String> postServiceFallback() {
        return Mono.just("Post Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/connectionService")
    public Mono<String> connectionServiceFallback() {
        return Mono.just("Connection Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/interactionService")
    public Mono<String> interactionServiceFallback() {
        return Mono.just("Interaction and Notification Service is taking too long to respond or is down. Please try again later.");
    }
}
