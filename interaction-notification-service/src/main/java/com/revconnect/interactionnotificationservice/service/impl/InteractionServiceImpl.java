package com.revconnect.interactionnotificationservice.service.impl;


import com.revconnect.interactionnotificationservice.entity.Interaction;
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

    @Override
    public String likePost(Long userId, Long postId) {

        Optional<Interaction> existingLike =
                interactionRepository.findByUserIdAndPostIdAndType(userId, postId, "LIKE");

        if (existingLike.isPresent()) {
            return "Post already liked";
        }

        Interaction interaction = Interaction.builder()
                .userId(userId)
                .postId(postId)
                .type("LIKE")
                .createdAt(LocalDateTime.now())
                .build();

        interactionRepository.save(interaction);

        Long postOwnerId = getPostOwner(postId);

        notificationService.createNotification(
                postOwnerId,
                userId,
                "LIKE",
                "User " + userId + " liked your post"
        );

        return "Post liked successfully";
    }

    public String unlikePost(Long userId, Long postId) {

        Optional<Interaction> existingLike =
                interactionRepository.findByUserIdAndPostIdAndType(userId, postId, "LIKE");

        if (existingLike.isEmpty()) {
            return "Like not found";
        }

        interactionRepository.delete(existingLike.get());

        return "Post unliked successfully";
    }

    //Temporary fix util the integration
    private Long getPostOwner(Long postId) {
        return 1L; // temporary
    }
}