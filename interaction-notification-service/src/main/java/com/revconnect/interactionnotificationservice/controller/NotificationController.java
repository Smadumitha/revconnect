package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.dto.NotificationPreferencesDTO;
import com.revconnect.interactionnotificationservice.entity.Notification;
import com.revconnect.interactionnotificationservice.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServiceImpl notificationService;



    @GetMapping("/{userId}")
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificationService.getUserNotificationsPaged(userId, pageable));
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Success", "Notification marked as read"));
    }

    @GetMapping("/unread-count")
    public Long getUnreadCount(@RequestParam Long userId){
        return notificationService.getUnreadCount(userId);
    }
    @PutMapping("/read-all")
    public String markAllRead(@RequestParam Long userId){
        notificationService.markAllAsRead(userId);
        return "All notifications marked as read";
    }

    @GetMapping("/preferences/{userId}")
    public ResponseEntity<NotificationPreferencesDTO> getPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getPreferences(userId));
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<NotificationPreferencesDTO> updatePreferences(
            @PathVariable Long userId,
            @RequestBody NotificationPreferencesDTO prefs) {
        return ResponseEntity.ok(notificationService.updatePreferences(userId, prefs));
    }
}