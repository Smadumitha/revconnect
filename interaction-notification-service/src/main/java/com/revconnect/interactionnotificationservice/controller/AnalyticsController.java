package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.entity.Analytics;
import com.revconnect.interactionnotificationservice.service.impl.AnalyticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsServiceImpl analyticsService;

    @GetMapping("/post/{postId}")
    public List<Analytics> getPostAnalytics(@PathVariable Long postId) {
        return analyticsService.getPostAnalytics(postId);
    }

    @GetMapping("/engagement/{postId}")
    public Double getEngagement(
            @PathVariable Long postId,
            @RequestParam Long followers
    ) {
        return analyticsService.calculateEngagement(postId, followers);
    }
}