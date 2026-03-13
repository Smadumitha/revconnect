package com.revconnect.postfeedservice.controller;

import com.revconnect.postfeedservice.dto.PostResponse;
import com.revconnect.postfeedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    @GetMapping("/test")
    public String test(){
        return "feed service working";
    }
    // Personalized Feed
    @GetMapping("/home")
    public org.springframework.data.domain.Page<PostResponse> homeFeed(@RequestParam Long userId, org.springframework.data.domain.Pageable pageable) {

        return feedService.getHomeFeed(userId, pageable);
    }
    @GetMapping("/trending-tags")
    public List<String> trendingTags(){
        return feedService.getTrendingTags();
    }
    // Trending Posts
    @GetMapping("/trending")
    public org.springframework.data.domain.Page<PostResponse> trendingPosts(@RequestParam(required = false) Long userId, org.springframework.data.domain.Pageable pageable) {

        return feedService.getTrendingPosts(userId, pageable);
    }

    // Search Posts By Hashtag
    @GetMapping("/hashtag")
    public org.springframework.data.domain.Page<PostResponse> searchByHashtag(@RequestParam String tag, @RequestParam(required = false) Long userId, org.springframework.data.domain.Pageable pageable) {

        return feedService.searchByHashtag(tag, userId, pageable);
    }

    // Promotional Posts
    @GetMapping("/promotional")
    public org.springframework.data.domain.Page<PostResponse> promotionalPosts(@RequestParam(required = false) Long userId, org.springframework.data.domain.Pageable pageable) {

        return feedService.getPromotionalPosts(userId, pageable);
    }
}