package com.homeservice.homecraft_backend.model.dto.request;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectSearchRequest {
    private String keyword;          // Search by title, description
    private String projectType;      // NEW_CONSTRUCTION, RENOVATION, FINISHING
    private ProfessionalType professionalTypeNeeded;
    private String location;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private String status;           // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    private String sortBy;           // createdAt, budgetMin, budgetMax
    private String sortDirection;    // asc, desc
    private Integer page = 0;
    private Integer size = 10;
}