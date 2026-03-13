package com.revconnect.interactionnotificationservice.dto;

import lombok.Data;

@Data
public class CommentRequestDTO {

    private Long userId;

    private Long postId;

    private String content;

    private Long parentCommentId;
}