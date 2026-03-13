package com.revconnect.interactionnotificationservice.service;

import com.revconnect.interactionnotificationservice.entity.Analytics;
import com.revconnect.interactionnotificationservice.dto.UserAnalyticsDTO;

import java.util.List;

public interface AnalyticsService {

    void updateLikes(Long postId);

    void updateComments(Long postId);

    void updateShares(Long postId);

    List<Analytics> getPostAnalytics(Long postId);

    Double calculateEngagement(Long postId, Long followers);

    UserAnalyticsDTO getUserAnalytics(Long userId, Long followers);

    void incrementProfileViews(Long userId);

    void incrementImpressions(Long postId, Long userId);
}