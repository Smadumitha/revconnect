package com.revconnect.interactionnotificationservice.service;

public interface InteractionService {
    public String likePost(Long userId, Long postId);

    public String unlikePost(Long userId, Long postId);

    public long getLikeCount(Long postId);

    public boolean hasLiked(Long userId, Long postId);

    java.util.List<String> getLikerNames(Long postId);
}
