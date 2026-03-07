package com.revconnect.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 500)
    private String bio;

    private String location;

    private String website;

    private String profilePictureUrl;

    private Boolean isPrivate = false;
}