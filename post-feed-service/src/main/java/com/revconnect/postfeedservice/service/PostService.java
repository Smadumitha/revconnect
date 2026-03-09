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

    private final PostHashtagRepository postHashtagRepository;
    private final com.revconnect.postfeedservice.client.InteractionClient interactionClient;
    private final com.revconnect.postfeedservice.client.UserClient userClient;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final ProductTagRepository productTagRepository;
    private final ScheduledPostRepository scheduledPostRepository;

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
        
        // ---------- PRODUCT TAG LOGIC ----------
        saveProductTags(savedPost, request);

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
    public PostResponse getPostById(Long id, Long currentUserId) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return toResponse(post, currentUserId);
    }
    
    public PostResponse getPostById(Long id) {
        return getPostById(id, null);
    }

    // Entity → DTO mapper
    public PostResponse toResponse(Post post, Long currentUserId) {
        long likesCount = 0;
        long commentsCount = 0;
        long sharesCount = 0;
        boolean isLiked = false;
        boolean isShared = false;
        com.revconnect.postfeedservice.dto.AuthorDTO author = null;
        
        try {
            likesCount = interactionClient.getLikeCount(post.getId());
            commentsCount = interactionClient.getCommentCount(post.getId());
            sharesCount = interactionClient.getShareCount(post.getId());
            
            if (currentUserId != null) {
                isLiked = interactionClient.hasLiked(currentUserId, post.getId());
                isShared = interactionClient.hasShared(currentUserId, post.getId());
            }

            // Fetch author info
            author = userClient.getUserById(post.getUserId());
        } catch (Exception e) {
            // Log or ignore — keep counts at 0, author null
        }

        // FETCH HASHTAGS MANUALLY FROM REPOSITORY
        List<String> hashtags = postHashtagRepository.findByPostId(post.getId()).stream()
                .map(ph -> hashtagRepository.findById(ph.getHashtagId()).map(Hashtag::getTag).orElse(""))
                .filter(tag -> !tag.isEmpty())
                .toList();

        // FETCH PRODUCT TAGS MANUALLY FROM REPOSITORY
        List<String> productTags = productTagRepository.findByPostId(post.getId()).stream()
                .map(ProductTag::getProductName)
                .toList();

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .type(post.getType() != null ? post.getType() : "TEXT")
                .status(post.getStatus() != null ? post.getStatus() : "PUBLISHED")
                .promotional(post.getPromotional())
                .pinned(post.getPinned())
                .ctaText(post.getCtaText())
                .ctaUrl(post.getCtaUrl())
                .hashtags(hashtags)
                .productTags(productTags)
                .likesCount((int)likesCount)
                .commentsCount((int)commentsCount)
                .sharesCount((int)sharesCount)
                .isLiked(isLiked)
                .isShared(isShared)
                .author(author)
                .scheduledAt(post.getScheduledAt() != null ? post.getScheduledAt().toString() : null)
                .createdAt(post.getCreatedAt() != null ? post.getCreatedAt().toString() : null)
                .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null)
                .build();
    }

    private PostResponse mapToResponse(Post post) {
        return toResponse(post, null);
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
            
            postHashtagRepository.save(postHashtag);

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
    public List<PostResponse> getPostsByUser(Long userId, Long currentUserId){

        List<Post> posts = postRepository.findByUserId(userId);

        return posts.stream()
                .map(p -> toResponse(p, currentUserId))
                .toList();
    }

    public void updateMediaUrl(Long postId, String url) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setMediaUrl(url);
        postRepository.save(post);
    }

}