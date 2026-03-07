package com.revconnect.interactionnotificationservice.client;

import org.springframework.stereotype.Component;

@Component
public class PostServiceClientFallback implements PostServiceClient {

    @Override
    public Long getPostOwnerId(Long postId) {
        System.err.println("Post Service is down! Circuit Breaker fallback triggered for post " + postId);
        throw new RuntimeException("Post Service unavailable");
    }
}
