package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.entity.Share;
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


    @Override
    public String sharePost(Long userId, Long postId) {

        Optional<Share> existingShare =
                shareRepository.findByUserIdAndPostId(userId, postId);

        if (existingShare.isPresent()) {
            return "Post already shared";
        }

        Share share = Share.builder()
                .userId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();

        shareRepository.save(share);

        Long postOwnerId = getPostOwner(postId);

        notificationService.createNotification(
                postOwnerId,
                userId,
                "SHARE",
                "User " + userId + " shared your post"
        );

        return "Post shared successfully";
    }

    @Override
    public String undoShare(Long userId, Long postId) {

        Optional<Share> existingShare =
                shareRepository.findByUserIdAndPostId(userId, postId);

        if (existingShare.isEmpty()) {
            return "Share not found";
        }

        shareRepository.delete(existingShare.get());

        return "Share removed successfully";
    }


    //temporary fix until the integration
    private Long getPostOwner(Long postId) {
        // Temporary placeholder until Post Service integration
        return 1L;
    }
}