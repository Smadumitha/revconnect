package com.revconnect.connectionservice.client;

import com.revconnect.connectionservice.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    UserProfileResponse getUserProfile(@PathVariable("userId")  Long userId);

}