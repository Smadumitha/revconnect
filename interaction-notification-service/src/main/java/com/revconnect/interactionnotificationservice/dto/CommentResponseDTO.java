package com.revconnect.interactionnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private UserDTO author;
}
