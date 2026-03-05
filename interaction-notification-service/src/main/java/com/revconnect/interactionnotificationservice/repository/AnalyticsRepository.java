package com.revconnect.interactionnotificationservice.repository;

import com.revconnect.interactionnotificationservice.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    Optional<Analytics> findByPostIdAndDate(Long postId, LocalDate date);

    List<Analytics> findByPostIdOrderByDateAsc(Long postId);
}