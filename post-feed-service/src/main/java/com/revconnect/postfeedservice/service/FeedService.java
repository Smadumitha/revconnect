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

    public List<PostResponse> getHomeFeed(Long userId){

        List<FollowerResponse> followers = connectionClient.getFollowingUsers(userId);

        List<Long> followingIds = followers.stream()
                .map(FollowerResponse::getFollowingId)
                .toList();

        if(followingIds.isEmpty()){
            return List.of();
        }

        List<Post> posts = postRepository.findByUserIdIn(followingIds);

        return posts.stream()
                .map(p -> postService.toResponse(p, userId))
                .toList();
    }

    public List<PostResponse> getTrendingPosts(Long userId){

        List<Post> posts = postRepository.findTop10ByOrderByCreatedAtDesc();

        return posts.stream()
                .map(p -> postService.toResponse(p, userId))
                .toList();
    }
    public List<PostResponse> searchByHashtag(String tag, Long userId){

        List<Post> posts = postRepository.findPostsByHashtag(tag);

        return posts.stream()
                .map(p -> postService.toResponse(p, userId))
                .toList();
    }
    public List<PostResponse> getPromotionalPosts(Long userId){

        return postRepository.findByPromotionalTrue()
                .stream()
                .map(p -> postService.toResponse(p, userId))
                .toList();
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