package com.revconnect.interactionnotificationservice.service;

public interface InteractionService {
    public String likePost(Long userId, Long postId);

    public String unlikePost(Long userId, Long postId);
}
