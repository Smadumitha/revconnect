package com.revconnect.interactionnotificationservice.repository;

import com.revconnect.interactionnotificationservice.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {

    Optional<Share> findByUserIdAndPostId(Long userId, Long postId);

    long countByPostId(Long postId);
}