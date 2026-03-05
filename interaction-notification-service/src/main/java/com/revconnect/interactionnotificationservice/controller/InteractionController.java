package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.service.impl.InteractionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionServiceImpl interactionService;

    @PostMapping("/like")
    public String likePost(
            @RequestParam Long userId,
            @RequestParam Long postId
    ) {
        return interactionService.likePost(userId, postId);
    }

    @DeleteMapping("/unlike")
    public String unlikePost(
            @RequestParam Long userId,
            @RequestParam Long postId
    ) {
        return interactionService.unlikePost(userId, postId);
    }
}