package com.homeservice.homecraft_backend.model.entity;

import com.homeservice.homecraft_backend.model.enums.BidStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Column(name = "bid_amount", nullable = false)
    private BigDecimal bidAmount;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private BidStatus status = BidStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}