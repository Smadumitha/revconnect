package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.client.ConnectionClient;
import com.revconnect.postfeedservice.dto.FollowerResponse;
import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.entity.Post;
import com.revconnect.postfeedservice.repository.HashtagRepository;
import com.revconnect.postfeedservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final ConnectionClient connectionClient;
    private final HashtagRepository hashtagRepository;
    private final PostService postService;

    public org.springframework.data.domain.Page<PostResponse> getHomeFeed(Long userId, org.springframework.data.domain.Pageable pageable){

        List<com.revconnect.postfeedservice.dto.FollowerResponse> followers = connectionClient.getFollowingUsers(userId);

        List<Long> followingIds = followers.stream()
                .map(com.revconnect.postfeedservice.dto.FollowerResponse::getFollowingId)
                .toList();

        if(followingIds.isEmpty()){
            return org.springframework.data.domain.Page.empty();
        }

        return postRepository.findByUserIdIn(followingIds, pageable)
                .map(p -> postService.toResponse(p, userId));
    }

    public org.springframework.data.domain.Page<PostResponse> getTrendingPosts(Long userId, org.springframework.data.domain.Pageable pageable){

        return postRepository.findByOrderByCreatedAtDesc(pageable)
                .map(p -> postService.toResponse(p, userId));
    }
    public org.springframework.data.domain.Page<PostResponse> searchByHashtag(String tag, Long userId, org.springframework.data.domain.Pageable pageable){

        return postRepository.findPostsByHashtag(tag, pageable)
                .map(p -> postService.toResponse(p, userId));
    }
    public org.springframework.data.domain.Page<PostResponse> getPromotionalPosts(Long userId, org.springframework.data.domain.Pageable pageable){

        return postRepository.findByPromotionalTrue(pageable)
                .map(p -> postService.toResponse(p, userId));
    }

    public List<String> getTrendingTags(){
        List<String> tags = hashtagRepository.findTrendingTags();
        if (tags.size() < 3) {
            // Static fallbacks
            return List.of("RevConnect", "Tech", "Business", "Innovation");
        }
        return tags;
    }
}