package com.revconnect.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "privacy_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivacySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Boolean isPrivateAccount = false;
}