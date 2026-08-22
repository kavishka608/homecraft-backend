package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.NotificationRequest;
import com.homeservice.homecraft_backend.model.dto.response.NotificationCountResponse;
import com.homeservice.homecraft_backend.model.dto.response.NotificationResponse;
import com.homeservice.homecraft_backend.model.entity.Notification;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.repository.NotificationRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getReferenceId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setReferenceId(request.getReferenceId());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return mapToResponse(savedNotification);
    }

    @Transactional
    public NotificationResponse createNotificationForUser(Long userId, String title, String message, String type, Long referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return mapToResponse(savedNotification);
    }

    public Page<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::mapToResponse);
    }

    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NotificationCountResponse getNotificationCount(Long userId) {
        long total = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new NotificationCountResponse(total, total);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.markAsRead(notificationId, userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }

    // Helper methods for creating notifications with emails

    public void notifyNewBid(Long clientId, String clientEmail, String clientName,
                             String professionalName, String projectTitle, Double bidAmount, Long projectId) {
        // Create in-app notification
        String title = "New Bid on Your Project";
        String message = String.format("%s has placed a bid of $%.2f on your project '%s'",
                professionalName, bidAmount, projectTitle);
        createNotificationForUser(clientId, title, message, "BID_RECEIVED", projectId);

        // Send email
        emailService.sendNewBidEmail(clientEmail, clientName, professionalName, projectTitle, bidAmount);
    }

    public void notifyBidAccepted(Long professionalId, String professionalEmail, String professionalName, String projectTitle, Long projectId) {
        // Create in-app notification
        String title = "Your Bid Was Accepted! 🎉";
        String message = String.format("Your bid on project '%s' has been accepted!", projectTitle);
        createNotificationForUser(professionalId, title, message, "BID_ACCEPTED", projectId);

        // Send email
        emailService.sendBidAcceptedEmail(professionalEmail, professionalName, projectTitle);
    }

    public void notifyBidRejected(Long professionalId, String professionalEmail, String professionalName, String projectTitle, Long projectId) {
        // Create in-app notification
        String title = "Bid Update";
        String message = String.format("Your bid on project '%s' has been rejected.", projectTitle);
        createNotificationForUser(professionalId, title, message, "BID_REJECTED", projectId);

        // Send email
        emailService.sendBidRejectedEmail(professionalEmail, professionalName, projectTitle);
    }

    public void notifyProjectCompleted(Long professionalId, String professionalEmail, String professionalName, String projectTitle, Long projectId) {
        // Create in-app notification
        String title = "Project Completed";
        String message = String.format("Project '%s' has been marked as completed!", projectTitle);
        createNotificationForUser(professionalId, title, message, "PROJECT_COMPLETED", projectId);

        // Send email
        emailService.sendProjectCompletedEmail(professionalEmail, professionalName, projectTitle);
    }

    public void notifyNewReview(Long professionalId, String professionalEmail, String professionalName,
                                String projectTitle, int rating, Long reviewId) {
        // Create in-app notification
        String title = "New Review Received! ⭐";
        String message = String.format("You received a %d/5 star review on project '%s'", rating, projectTitle);
        createNotificationForUser(professionalId, title, message, "REVIEW_RECEIVED", reviewId);

        // Send email
        emailService.sendNewReviewEmail(professionalEmail, professionalName, projectTitle, rating);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setRead(notification.isRead());
        response.setType(notification.getType());
        response.setReferenceId(notification.getReferenceId());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}