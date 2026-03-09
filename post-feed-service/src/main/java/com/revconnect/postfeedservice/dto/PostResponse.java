package com.revconnect.postfeedservice.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
@Data
@Builder
public class PostResponse {
    private Long id;
    private Long userId;
    private String content;
    private String mediaUrl;
    private String type;            // NEW — TEXT, IMAGE, SHARE, PROMOTIONAL
    private String status;          // NEW — PUBLISHED, DRAFT, SCHEDULED
    private Boolean promotional;
    private Boolean pinned;
    private String ctaText;
    private String ctaUrl;
    private List<String> hashtags;    // NEW
    private List<String> productTags; // NEW
    private int likesCount;           // NEW — fetched from interaction-service via Feign
    private int commentsCount;        // NEW — fetched from interaction-service via Feign
    private int sharesCount;          // NEW — fetched from interaction-service via Feign
    private boolean isLiked;          // NEW — requires calling userId context
    private boolean isShared;         // NEW
    private AuthorDTO author;         // NEW — nested author info
    private String scheduledAt;       // NEW
    private String createdAt;
    private String updatedAt;
}