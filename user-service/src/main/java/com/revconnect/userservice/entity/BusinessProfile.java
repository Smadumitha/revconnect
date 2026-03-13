package com.revconnect.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String businessName;

    private String industry;

    private String website;

    private String contactEmail;

    private Boolean verified = false;
}