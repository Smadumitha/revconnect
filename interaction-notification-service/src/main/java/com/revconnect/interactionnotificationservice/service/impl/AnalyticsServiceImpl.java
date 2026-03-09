package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.client.PostServiceClient;
import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import com.revconnect.interactionnotificationservice.dto.UserAnalyticsDTO;
import com.revconnect.interactionnotificationservice.entity.Analytics;
import com.revconnect.interactionnotificationservice.repository.AnalyticsRepository;
import com.revconnect.interactionnotificationservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final PostServiceClient postServiceClient;

    private Analytics getOrCreate(Long postId, Long userId) {
        LocalDate today = LocalDate.now();
        
        if (postId != null) {
            return analyticsRepository.findByPostIdAndDate(postId, today)
                .orElseGet(() -> {
                    Long ownerId = userId;
                    if (ownerId == null) {
                        try {
                            ownerId = postServiceClient.getPostOwnerId(postId);
                        } catch (Exception e) {
                            // ignore or log
                        }
                    }
                    return analyticsRepository.save(Analytics.builder()
                        .postId(postId)
                        .userId(ownerId)
                        .date(today)
                        .likes(0L)
                        .comments(0L)
                        .shares(0L)
                        .newFollowers(0L)
                        .profileViews(0L)
                        .build());
                });
        } else {
            // Profile views (postId is null)
            return analyticsRepository.findByUserIdAndPostIdIsNullAndDate(userId, today)
                .orElseGet(() -> analyticsRepository.save(Analytics.builder()
                    .userId(userId)
                    .date(today)
                    .likes(0L)
                    .comments(0L)
                    .shares(0L)
                    .newFollowers(0L)
                    .profileViews(0L)
                    .build()));
        }
    }

    @Override
    public void updateLikes(Long postId) {
        Analytics analytics = getOrCreate(postId, null);
        analytics.setLikes(analytics.getLikes() + 1);
        analyticsRepository.save(analytics);
    }

    @Override
    public void updateComments(Long postId) {
        Analytics analytics = getOrCreate(postId, null);
        analytics.setComments(analytics.getComments() + 1);
        analyticsRepository.save(analytics);
    }

    @Override
    public void updateShares(Long postId) {
        Analytics analytics = getOrCreate(postId, null);
        analytics.setShares(analytics.getShares() + 1);
        analyticsRepository.save(analytics);
    }

    @Override
    public List<Analytics> getPostAnalytics(Long postId) {
        return analyticsRepository.findByPostIdOrderByDateAsc(postId);
    }

    @Override
    public Double calculateEngagement(Long postId, Long followers) {
        List<Analytics> stats = analyticsRepository.findByPostIdOrderByDateAsc(postId);
        long interactions = stats.stream().mapToLong(s -> s.getLikes() + s.getComments() + s.getShares()).sum();
        if (followers == null || followers == 0) return 0.0;
        return (double) interactions / followers * 100;
    }

    @Override
    public UserAnalyticsDTO getUserAnalytics(Long userId, Long followers) {
        List<Analytics> stats = analyticsRepository.findByUserId(userId);
        
        long likes = stats.stream().mapToLong(Analytics::getLikes).sum();
        long comments = stats.stream().mapToLong(Analytics::getComments).sum();
        long shares = stats.stream().mapToLong(Analytics::getShares).sum();
        long views = stats.stream().mapToLong(Analytics::getProfileViews).sum();
        
        // Mock impressions as views * factor for now if not tracked separately
        long impressions = likes * 5 + comments * 3 + views * 2;

        double engagement = (followers == null || followers == 0) ? 0.0 : 
            (double) (likes + comments + shares) / followers * 100;

        return UserAnalyticsDTO.builder()
            .totalLikes(likes)
            .totalComments(comments)
            .totalShares(shares)
            .totalProfileViews(views)
            .totalImpressions(impressions)
            .engagementRate(engagement)
            .build();
    }

    @Override
    public void incrementProfileViews(Long userId) {
        Analytics analytics = getOrCreate(null, userId);
        analytics.setProfileViews(analytics.getProfileViews() + 1);
        analyticsRepository.save(analytics);
    }

    @Override
    public void incrementImpressions(Long postId, Long userId) {
        // Impressions can be tracked per post day
        Analytics analytics = getOrCreate(postId, userId);
        // Using profileViews field as a proxy or we could add an impressions field
        // For now, let's just make sure we have a record
        analyticsRepository.save(analytics);
    }

    @EventListener
    public void consumeInteractionEvent(InteractionEvent event) {
        if ("LIKE".equals(event.getType())) {
            updateLikes(event.getPostId());
        } else if ("COMMENT".equals(event.getType())) {
            updateComments(event.getPostId());
        } else if ("SHARE".equals(event.getType())) {
            updateShares(event.getPostId());
        } else if ("UNLIKE".equals(event.getType())) {
            Analytics analytics = getOrCreate(event.getPostId(), null);
            if (analytics.getLikes() > 0) {
                analytics.setLikes(analytics.getLikes() - 1);
                analyticsRepository.save(analytics);
            }
        }
    }
}