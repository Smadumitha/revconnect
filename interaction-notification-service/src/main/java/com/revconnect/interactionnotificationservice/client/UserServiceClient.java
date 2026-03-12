package com.revconnect.interactionnotificationservice.client;

import com.revconnect.interactionnotificationservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url:http://localhost:8081}")
public interface UserServiceClient {
    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
