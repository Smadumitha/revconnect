package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.client.PostServiceClient;
import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import com.revconnect.interactionnotificationservice.entity.Share;
import com.revconnect.interactionnotificationservice.event.InteractionEventProducer;
import com.revconnect.interactionnotificationservice.exception.ConflictException;
import com.revconnect.interactionnotificationservice.exception.ResourceNotFoundException;
import com.revconnect.interactionnotificationservice.repository.ShareRepository;
import com.revconnect.interactionnotificationservice.service.NotificationService;
import com.revconnect.interactionnotificationservice.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final NotificationService notificationService;
    private final PostServiceClient postServiceClient;
    private final InteractionEventProducer interactionEventProducer;

    @Override
    public String sharePost(Long userId, Long postId) {

        Optional<Share> existingShare = shareRepository.findByUserIdAndPostId(userId, postId);

        if (existingShare.isPresent()) {
            throw new ConflictException("Post already shared");
        }

        Share share = Share.builder()
                .userId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();

        shareRepository.save(share);

        interactionEventProducer.sendInteractionEvent(new InteractionEvent(postId, userId, "SHARE"));

        Long postOwnerId;
        try {
            postOwnerId = postServiceClient.getPostOwnerId(postId);
        } catch (Exception e) {
            postOwnerId = 1L; // Fallback
        }

        notificationService.createNotification(
                postOwnerId,
                userId,
                "SHARE",
                "User " + userId + " shared your post");

        return "Post shared successfully";
    }

    @Override
    public String undoShare(Long userId, Long postId) {

        Optional<Share> existingShare = shareRepository.findByUserIdAndPostId(userId, postId);

        if (existingShare.isEmpty()) {
            throw new ResourceNotFoundException("Share not found");
        }

        shareRepository.delete(existingShare.get());
        return "Share removed successfully";
    }

    @Override
    public long getShareCount(Long postId) {
        return shareRepository.countByPostId(postId);
    }

    @Override
    public boolean hasShared(Long userId, Long postId) {
        return shareRepository.existsByUserIdAndPostId(userId, postId);
    }
}