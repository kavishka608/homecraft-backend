package com.homeservice.homecraft_backend.model.dto.response;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String projectType;
    private ProfessionalType professionalTypeNeeded;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String location;
    private LocalDate expectedStartDate;
    private LocalDate expectedEndDate;
    private String status;
    private UserResponse client;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}