package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.dto.ShareRequestDTO;
import com.revconnect.interactionnotificationservice.service.impl.ShareServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareServiceImpl shareService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> sharePost(
            @RequestBody ShareRequestDTO request) {
        String result = shareService.sharePost(request.getUserId(), request.getPostId());
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> undoShare(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        String result = shareService.undoShare(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @GetMapping("/count")
    public long getShareCount(@RequestParam Long postId) {
        return shareService.getShareCount(postId);
    }

    @GetMapping("/has-shared")
    public boolean hasShared(@RequestParam Long userId, @RequestParam Long postId) {
        return shareService.hasShared(userId, postId);
    }

    @PostMapping("/repost")
    public ResponseEntity<ApiResponse<String>> repost(
            @RequestBody java.util.Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long postId = Long.valueOf(body.get("postId").toString());
        // Record the repost as a share
        String result = shareService.sharePost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("Reposted", result));
    }
}