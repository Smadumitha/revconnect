package com.revconnect.postfeedservice.client;

import com.revconnect.postfeedservice.dto.FollowerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="connection-service")
public interface ConnectionClient {

    @GetMapping("/connections/following/{userId}")
    List<FollowerResponse> getFollowingUsers(@PathVariable("userId") Long userId);

}