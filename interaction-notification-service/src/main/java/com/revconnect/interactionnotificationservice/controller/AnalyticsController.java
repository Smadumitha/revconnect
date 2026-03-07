package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.entity.Analytics;
import com.revconnect.interactionnotificationservice.service.impl.AnalyticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsServiceImpl analyticsService;

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
}