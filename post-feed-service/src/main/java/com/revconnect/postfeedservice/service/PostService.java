package com.revconnect.postfeedservice.service;

import com.revconnect.postfeedservice.dto.PostRequest;
import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.entity.*;
import com.revconnect.postfeedservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final ProductTagRepository productTagRepository;
    private final ScheduledPostRepository scheduledPostRepository;
    private  final PostHashtagRepository postHashtagRepository;


    public PostResponse createPost(PostRequest request) {

        Post post = Post.builder()
                .userId(request.getUserId())
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .promotional(request.getPromotional())
                .pinned(request.getPinned())
                .createdAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);

        // ---------- HASHTAG LOGIC ----------
        List<String> tags = extractHashtags(request.getContent());

        for (String tag : tags) {

            Hashtag hashtag = hashtagRepository.findByTag(tag)
                    .orElseGet(() -> {
                        Hashtag newTag = new Hashtag();
                        newTag.setTag(tag);
                        return hashtagRepository.save(newTag);
                    });

            PostHashtag postHashtag = new PostHashtag();
            postHashtag.setPostId(savedPost.getId());
            postHashtag.setHashtagId(hashtag.getId());

            postHashtagRepository.save(postHashtag);
        }

        // ---------- SCHEDULING LOGIC ----------
        if (request.getScheduledAt() != null) {

            ScheduledPost scheduledPost = new ScheduledPost();
            scheduledPost.setPostId(savedPost.getId());
            scheduledPost.setScheduledTime(request.getScheduledAt());
            scheduledPost.setPublished(false);

            scheduledPostRepository.save(scheduledPost);
        }

        return mapToResponse(savedPost);
    }
    // Update Post
    public PostResponse updatePost(Long id, PostRequest request) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        post.setPromotional(request.getPromotional());
        post.setPinned(request.getPinned());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        return mapToResponse(updatedPost);
    }

    // Delete Post
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(post);
    }

    // Get Post By ID
    public PostResponse getPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return mapToResponse(post);
    }

    // Entity → DTO mapper
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

    private List<String> extractHashtags(String content) {

        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);

        List<String> tags = new ArrayList<>();

        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        return tags;
    }

    private void saveHashtags(Post post){

        List<String> tags = extractHashtags(post.getContent());

        for(String tag : tags){

            Hashtag hashtag = hashtagRepository
                    .findByTag(tag)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(null, tag)));

            PostHashtag postHashtag = new PostHashtag();
            postHashtag.setPostId(post.getId());
            postHashtag.setHashtagId(hashtag.getId());

        }
    }
    private void saveProductTags(Post post, PostRequest request){

        if(request.getProductTags() == null) return;

        for(String product : request.getProductTags()){

            ProductTag tag = new ProductTag();

            tag.setPostId(post.getId());
            tag.setProductName(product);

            productTagRepository.save(tag);
        }
    }
    private void schedulePost(Post post){

        if(post.getScheduledAt() == null) return;

        ScheduledPost scheduledPost = new ScheduledPost();

        scheduledPost.setPostId(post.getId());
        scheduledPost.setScheduledTime(post.getScheduledAt());
        scheduledPost.setPublished(false);

        scheduledPostRepository.save(scheduledPost);
    }
    public Long getPostOwnerId(Long postId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return post.getUserId();
    }
}