package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.service.impl.ShareServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareServiceImpl shareService;

    @PostMapping
    public String sharePost(
            @RequestParam Long userId,
            @RequestParam Long postId
    ) {
        return shareService.sharePost(userId, postId);
    }

    @DeleteMapping
    public String undoShare(
            @RequestParam Long userId,
            @RequestParam Long postId
    ) {
        return shareService.undoShare(userId, postId);
    }
}