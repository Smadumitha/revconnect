package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.dto.UserAnalyticsDTO;
import com.revconnect.interactionnotificationservice.entity.Analytics;
import com.revconnect.interactionnotificationservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<Analytics>>> getPostAnalytics(@PathVariable Long postId) {
        List<Analytics> result = analyticsService.getPostAnalytics(postId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @GetMapping("/engagement/{postId}")
    public ResponseEntity<ApiResponse<Double>> getEngagement(
            @PathVariable Long postId,
            @RequestParam Long followers) {
        Double result = analyticsService.calculateEngagement(postId, followers);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserAnalyticsDTO>> getUserAnalytics(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") Long followers) {
        UserAnalyticsDTO result = analyticsService.getUserAnalytics(userId, followers);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @PostMapping("/view/profile/{userId}")
    public ResponseEntity<ApiResponse<String>> trackProfileView(@PathVariable Long userId) {
        analyticsService.incrementProfileViews(userId);
        return ResponseEntity.ok(ApiResponse.success("Success", "View tracked"));
    }
}