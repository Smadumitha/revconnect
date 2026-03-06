package com.revconnect.postfeedservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {

    private Long id;
    private Long userId;
    private String content;
    private String mediaUrl;
    private Boolean promotional;
    private Boolean pinned;
    private LocalDateTime createdAt;

}