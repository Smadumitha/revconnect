package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.client.PostServiceClient;
import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import com.revconnect.interactionnotificationservice.entity.Interaction;
import com.revconnect.interactionnotificationservice.event.InteractionEventProducer;
import com.revconnect.interactionnotificationservice.exception.ConflictException;
import com.revconnect.interactionnotificationservice.exception.ResourceNotFoundException;
import com.revconnect.interactionnotificationservice.repository.InteractionRepository;
import com.revconnect.interactionnotificationservice.service.InteractionService;
import com.revconnect.interactionnotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final NotificationService notificationService;
    private final PostServiceClient postServiceClient;
    private final InteractionEventProducer interactionEventProducer;

    @Override
    public String likePost(Long userId, Long postId) {
        Long postOwnerId;

        try {
            postOwnerId = postServiceClient.getPostOwnerId(postId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Post not found");
        }

        if(postOwnerId == null){
            throw new ResourceNotFoundException("Post not found");
        }
        Optional<Interaction> existingLike = interactionRepository.findByUserIdAndPostIdAndType(userId, postId, "LIKE");

        if (existingLike.isPresent()) {
            throw new ConflictException("Post already liked");
        }

        Interaction interaction = Interaction.builder()
                .userId(userId)
                .postId(postId)
                .type("LIKE")
                .createdAt(LocalDateTime.now())
                .build();

        interactionRepository.save(interaction);

        interactionEventProducer.sendInteractionEvent(new InteractionEvent(postId, userId, "LIKE"));

        // Send event for analytics
        interactionEventProducer.sendInteractionEvent(
                new InteractionEvent(postId, userId, "LIKE")
        );

        notificationService.createNotification(
                postOwnerId,
                userId,
                "LIKE",
                "User " + userId + " liked your post");

        return "Post liked successfully";
    }

    public String unlikePost(Long userId, Long postId) {

        Optional<Interaction> existingLike = interactionRepository.findByUserIdAndPostIdAndType(userId, postId, "LIKE");

        if (existingLike.isEmpty()) {
            throw new ResourceNotFoundException("Like not found");
        }

        interactionRepository.delete(existingLike.get());

        interactionEventProducer.sendInteractionEvent(new InteractionEvent(postId, userId, "UNLIKE"));

        return "Post unliked successfully";
    }
}