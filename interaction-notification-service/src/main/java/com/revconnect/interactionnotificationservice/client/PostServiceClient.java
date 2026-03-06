package com.revconnect.interactionnotificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Removed hardcoded URL so Spring Cloud LoadBalancer uses Eureka to find 'post-service'
@FeignClient(name = "post-service", path = "/api/posts")
public interface PostServiceClient {

    @GetMapping("/{postId}/owner")
    Long getPostOwnerId(@PathVariable("postId") Long postId);
}
