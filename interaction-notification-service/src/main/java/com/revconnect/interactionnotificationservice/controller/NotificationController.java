package com.revconnect.interactionnotificationservice.controller;

import com.revconnect.interactionnotificationservice.dto.ApiResponse;
import com.revconnect.interactionnotificationservice.entity.Notification;
import com.revconnect.interactionnotificationservice.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> result = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
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
}