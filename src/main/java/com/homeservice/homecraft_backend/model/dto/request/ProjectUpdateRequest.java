package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectUpdateRequest {
    private String title;
    private String description;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String location;
    private LocalDate expectedStartDate;
    private LocalDate expectedEndDate;
    private String status;  // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
}