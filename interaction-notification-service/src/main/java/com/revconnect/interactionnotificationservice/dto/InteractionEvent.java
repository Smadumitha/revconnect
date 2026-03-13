package com.revconnect.interactionnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionEvent {
    private Long postId;
    private Long userId;
    private String type; // LIKE, COMMENT, SHARE
}
