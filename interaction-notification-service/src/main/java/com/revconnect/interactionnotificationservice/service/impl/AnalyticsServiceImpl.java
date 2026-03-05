package com.revconnect.interactionnotificationservice.service.impl;

import com.revconnect.interactionnotificationservice.entity.Analytics;
import com.revconnect.interactionnotificationservice.repository.AnalyticsRepository;
import com.revconnect.interactionnotificationservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    private Analytics getOrCreate(Long postId) {

        LocalDate today = LocalDate.now();

        return analyticsRepository.findByPostIdAndDate(postId, today)
                .orElseGet(() -> Analytics.builder()
                        .postId(postId)
                        .date(today)
                        .likes(0L)
                        .comments(0L)
                        .shares(0L)
                        .build());
    }

    @Override
    public void updateLikes(Long postId) {

        Analytics analytics = getOrCreate(postId);

        analytics.setLikes(analytics.getLikes() + 1);

        analyticsRepository.save(analytics);
    }

    @Override
    public void updateComments(Long postId) {

        Analytics analytics = getOrCreate(postId);

        analytics.setComments(analytics.getComments() + 1);

        analyticsRepository.save(analytics);
    }

    @Override
    public void updateShares(Long postId) {

        Analytics analytics = getOrCreate(postId);

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

        long likes = stats.stream().mapToLong(Analytics::getLikes).sum();
        long comments = stats.stream().mapToLong(Analytics::getComments).sum();
        long shares = stats.stream().mapToLong(Analytics::getShares).sum();

        long interactions = likes + comments + shares;

        if (followers == 0) return 0.0;

        return (double) interactions / followers * 100;
    }
}