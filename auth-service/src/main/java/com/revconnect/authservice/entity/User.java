package com.revconnect.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;

    private String provider;

    private boolean privateAccount;

    private String securityQuestion;

    private String securityAnswer;

    private int failedAttempts;

    private boolean accountLocked;

}
