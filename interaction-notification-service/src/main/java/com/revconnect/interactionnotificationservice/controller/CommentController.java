package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.dto.CommentRequestDTO;
import com.revconnect.interactionnotificationservice.entity.Comment;
import com.revconnect.interactionnotificationservice.service.impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addComment(@RequestBody CommentRequestDTO request) {
        String result = commentService.addComment(request);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        String result = commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Page<Comment>>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> result = commentService.getPostComments(postId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
}