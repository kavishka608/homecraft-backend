package com.homeservice.homecraft_backend.model.entity;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "project_type")
    private String projectType;  // NEW_CONSTRUCTION, RENOVATION, FINISHING

    @Enumerated(EnumType.STRING)
    @Column(name = "professional_type_needed")
    private ProfessionalType professionalTypeNeeded;

    @Column(name = "budget_min")
    private BigDecimal budgetMin;

    @Column(name = "budget_max")
    private BigDecimal budgetMax;

    private String location;

    @Column(name = "expected_start_date")
    private LocalDate expectedStartDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    private String status = "OPEN";  // OPEN, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "OPEN";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}