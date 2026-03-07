package com.revconnect.authservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;

    private String username;

    private String password;

    private String role;

    private boolean privateAccount;

    private String securityQuestion;

    private String securityAnswer;

}
