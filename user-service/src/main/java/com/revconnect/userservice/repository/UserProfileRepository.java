package com.revconnect.userservice.repository;

import com.revconnect.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Find profile using userId
    Optional<UserProfile> findByUserId(Long userId);

    // Search users by username (used for search API)
    List<UserProfile> findByUsernameContainingIgnoreCase(String keyword);

    // Check if username already exists
    boolean existsByUsername(String username);

    Optional<UserProfile> findByUsername(String username);

    
}