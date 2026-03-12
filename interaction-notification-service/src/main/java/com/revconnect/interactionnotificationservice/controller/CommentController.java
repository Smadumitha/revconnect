package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.dto.CommentRequestDTO;
import com.revconnect.interactionnotificationservice.dto.CommentResponseDTO;
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

    //@PostMapping
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDTO>> addComment(@RequestBody CommentRequestDTO request) {
        CommentResponseDTO result = commentService.addComment(request);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        String result = commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    // CHANGED: Returns List<Comment> not Page<Comment> for simpler frontend integration
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<java.util.List<com.revconnect.interactionnotificationservice.dto.CommentResponseDTO>>> getPostComments(
            @PathVariable Long postId) {
        java.util.List<com.revconnect.interactionnotificationservice.dto.CommentResponseDTO> result = commentService.getPostCommentsList(postId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @GetMapping("/count")
    public long getCommentCount(@RequestParam Long postId) {
        return commentService.getCommentCount(postId);
    }
}