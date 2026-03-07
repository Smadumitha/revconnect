package com.revconnect.authservice.dto;

import lombok.Data;

@Data
public class SecurityAnswerRequest {

    private String username;
    private String answer;

}
