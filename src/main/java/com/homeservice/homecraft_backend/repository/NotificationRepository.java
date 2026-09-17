package com.homeservice.homecraft_backend.repository;

import com.homeservice.homecraft_backend.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsRead(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.user.id = :userId")
    void markAsRead(Long notificationId, Long userId);
}