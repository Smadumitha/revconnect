package com.revconnect.interactionnotificationservice.service;

import com.revconnect.interactionnotificationservice.dto.CommentRequestDTO;
import com.revconnect.interactionnotificationservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    String addComment(CommentRequestDTO request);

    String deleteComment(Long commentId);

    Page<Comment> getPostComments(Long postId, Pageable pageable);

    long getCommentCount(Long postId);

    java.util.List<Comment> getPostCommentsList(Long postId);
}