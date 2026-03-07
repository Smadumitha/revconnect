package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.client.PostServiceClient;
import com.revconnect.interactionnotificationservice.dto.CommentRequestDTO;
import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import com.revconnect.interactionnotificationservice.entity.Comment;
import com.revconnect.interactionnotificationservice.event.InteractionEventProducer;
import com.revconnect.interactionnotificationservice.exception.ResourceNotFoundException;
import com.revconnect.interactionnotificationservice.repository.CommentRepository;
import com.revconnect.interactionnotificationservice.service.CommentService;
import com.revconnect.interactionnotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final PostServiceClient postServiceClient;
    private final InteractionEventProducer interactionEventProducer;

    @Override
    public String addComment(CommentRequestDTO request) {

        Comment comment = Comment.builder()
                .userId(request.getUserId())
                .postId(request.getPostId())
                .content(request.getContent())
                .parentCommentId(request.getParentCommentId())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        interactionEventProducer
                .sendInteractionEvent(new InteractionEvent(request.getPostId(), request.getUserId(), "COMMENT"));

        Long postOwnerId;
        try {
            postOwnerId = postServiceClient.getPostOwnerId(request.getPostId());
        } catch (Exception e) {
            postOwnerId = 1L; // Fallback
        }

        notificationService.createNotification(
                postOwnerId,
                request.getUserId(),
                "COMMENT",
                "User " + request.getUserId() + " commented on your post");

        if (request.getParentCommentId() == null) {
            return "Comment added successfully";
        }

        return "Reply added successfully";
    }

    @Override
    public String deleteComment(Long commentId) {

        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found");
        }

        commentRepository.deleteById(commentId);

        return "Comment deleted successfully";
    }

    @Override
    public Page<Comment> getPostComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdAndParentCommentIdIsNull(postId, pageable);
    }
}