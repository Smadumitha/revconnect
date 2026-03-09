package com.revconnect.interactionnotificationservice.service;

public interface ShareService {

    String sharePost(Long userId, Long postId);

    String undoShare(Long userId, Long postId);

    long getShareCount(Long postId);

    boolean hasShared(Long userId, Long postId);
}