package com.revconnect.postfeedservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "INTERACTION-NOTIFICATION-SERVICE")
public interface InteractionClient {

    @GetMapping("/api/interactions/count")
    long getLikeCount(@RequestParam("postId") Long postId);

    @GetMapping("/api/comments/count")
    long getCommentCount(@RequestParam("postId") Long postId);

    @GetMapping("/api/shares/count")
    long getShareCount(@RequestParam("postId") Long postId);

    @GetMapping("/api/interactions/has-liked")
    boolean hasLiked(@RequestParam("userId") Long userId, @RequestParam("postId") Long postId);

    @GetMapping("/api/shares/has-shared")
    boolean hasShared(@RequestParam("userId") Long userId, @RequestParam("postId") Long postId);
}
