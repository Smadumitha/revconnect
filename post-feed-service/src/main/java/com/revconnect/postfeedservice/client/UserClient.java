package com.revconnect.postfeedservice.client;

import com.revconnect.postfeedservice.dto.AuthorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/username/{username}")
    AuthorDTO getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/users/{userId}")
    AuthorDTO getUserById(@PathVariable("userId") Long userId);
}
