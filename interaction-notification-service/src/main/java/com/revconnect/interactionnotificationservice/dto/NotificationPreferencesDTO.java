package com.revconnect.interactionnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDTO {
    private boolean connectionRequests;
    private boolean postLikes;
    private boolean postComments;
    private boolean postShares;
    private boolean newFollowers;
}
