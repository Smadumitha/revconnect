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

    // Personalized Feed
    @GetMapping("/home")
    public List<PostResponse> homeFeed(@RequestParam Long userId) {

        return feedService.getHomeFeed(userId);
    }
    @GetMapping("/trending-tags")
    public List<String> trendingTags(){
        return feedService.getTrendingTags();
    }
    // Trending Posts
    @GetMapping("/trending")
    public List<PostResponse> trendingPosts() {

        return feedService.getTrendingPosts();
    }

    // Search Posts By Hashtag
    @GetMapping("/hashtag")
    public List<PostResponse> searchByHashtag(@RequestParam String tag) {

        return feedService.searchByHashtag(tag);
    }

    // Promotional Posts
    @GetMapping("/promotional")
    public List<PostResponse> promotionalPosts() {

        return feedService.getPromotionalPosts();
    }
}