package com.revconnect.authservice.client;

import com.revconnect.authservice.dto.CreateUserProfileRequest;
import com.revconnect.authservice.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @PostMapping("/api/users")
    UserProfileResponse createUserProfile(@RequestBody CreateUserProfileRequest request);

}