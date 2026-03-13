package com.revconnect.connectionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionStatusDTO {
    private boolean isFollowing;
    private boolean isConnected;
    private boolean isPendingSent;
    private boolean isPendingReceived;
}
