package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.NotificationCountResponse;
import com.homeservice.homecraft_backend.model.dto.response.NotificationResponse;
import com.homeservice.homecraft_backend.service.NotificationService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(authHeader);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved successfully", notifications));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<NotificationCountResponse>> getNotificationCount(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        NotificationCountResponse count = notificationService.getNotificationCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Notification count retrieved successfully", count));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}