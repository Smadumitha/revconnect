package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.dto.InteractionRequestDTO;
import com.revconnect.interactionnotificationservice.service.impl.InteractionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionServiceImpl interactionService;

    @PostMapping("/like")
    public ResponseEntity<ApiResponse<String>> likePost(
            @RequestBody InteractionRequestDTO request) {
        String result = interactionService.likePost(request.getUserId(), request.getPostId());
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<ApiResponse<String>> unlikePost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        String result = interactionService.unlikePost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
}