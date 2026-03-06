package com.revconnect.connectionservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConnectionRequestDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String status;
    private LocalDateTime createdAt;
}