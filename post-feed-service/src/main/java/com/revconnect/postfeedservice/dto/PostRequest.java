package com.revconnect.postfeedservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class PostRequest {
    private Long userId;
    private String content;
    private String mediaUrl;
    private String type;            // NEW — TEXT, IMAGE, SHARE, PROMOTIONAL
    private List<String> hashtags;
    private List<String> productTags;
    private Boolean promotional;
    private Boolean pinned;
    private String ctaText;
    private String ctaUrl;
    private LocalDateTime scheduledAt;
}