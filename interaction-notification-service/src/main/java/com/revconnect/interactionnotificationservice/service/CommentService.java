package com.revconnect.interactionnotificationservice.service;

import com.revconnect.interactionnotificationservice.dto.CommentRequestDTO;
import com.revconnect.interactionnotificationservice.dto.CommentResponseDTO;
import com.revconnect.interactionnotificationservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    public CommentResponseDTO addComment(CommentRequestDTO request);

    String deleteComment(Long commentId);

    org.springframework.data.domain.Page<com.revconnect.interactionnotificationservice.dto.CommentResponseDTO> getPostComments(Long postId, Pageable pageable);

    long getCommentCount(Long postId);

    java.util.List<com.revconnect.interactionnotificationservice.dto.CommentResponseDTO> getPostCommentsList(Long postId);
}