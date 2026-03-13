package com.revconnect.interactionnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnalyticsDTO {
    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;
    private Long totalProfileViews;
    private Long totalImpressions;
    private Double engagementRate;
}
