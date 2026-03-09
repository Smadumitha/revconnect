package com.revconnect.interactionnotificationservice.repository;

import com.revconnect.interactionnotificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId); // RENAMED from userId
    
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable); // RENAMED from userId
    
    long countByReceiverIdAndIsReadFalse(Long receiverId); // RENAMED from userId
}