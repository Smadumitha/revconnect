package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.client.ConnectionClient;
import com.revconnect.postfeedservice.dto.FollowerResponse;
import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.entity.Post;
import com.revconnect.postfeedservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final ConnectionClient connectionClient;

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
                .map(this::mapToResponse)
                .toList();
    }
    public List<PostResponse> getTrendingPosts(){

        List<Post> posts = postRepository.findTop10ByOrderByCreatedAtDesc();

        return posts.stream()
                .map(this::mapToResponse)
                .toList();
    }
    public List<PostResponse> searchByHashtag(String tag){

        List<Post> posts = postRepository.findPostsByHashtag(tag);

        return posts.stream()
                .map(this::mapToResponse)
                .toList();
    }
    public List<PostResponse> getPromotionalPosts(){

        return postRepository.findByPromotionalTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .promotional(post.getPromotional())
                .pinned(post.getPinned())
                .createdAt(post.getCreatedAt())
                .build();
    }
}