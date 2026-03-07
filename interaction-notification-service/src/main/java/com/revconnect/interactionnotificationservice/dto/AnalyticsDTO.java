package com.revconnect.interactionnotificationservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDTO {

    private Long id;
    private Long userId;
    private Long postId;
    private LocalDate date;
    private Long likes;
    private Long comments;
    private Long shares;
    private Long newFollowers;
    private Long profileViews;
}
