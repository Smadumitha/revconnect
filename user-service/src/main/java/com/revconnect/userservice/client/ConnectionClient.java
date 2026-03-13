package com.revconnect.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "CONNECTION-SERVICE")
public interface ConnectionClient {

    @GetMapping("/connections/followers/{userId}")
    List<Object> getFollowers(@PathVariable("userId") Long userId);

    @GetMapping("/connections/following/{userId}")
    List<Object> getFollowing(@PathVariable("userId") Long userId);
}
