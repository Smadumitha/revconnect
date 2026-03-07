package com.revconnect.interactionnotificationservice.repository;

import com.revconnect.interactionnotificationservice.entity.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    Optional<Interaction> findByUserIdAndPostIdAndType(Long userId, Long postId, String type);

    long countByPostIdAndType(Long postId, String type);
}