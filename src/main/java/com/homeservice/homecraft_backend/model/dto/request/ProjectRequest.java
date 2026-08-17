package com.homeservice.homecraft_backend.model.dto.request;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectRequest {
    private String title;
    private String description;
    private String projectType;
    private ProfessionalType professionalTypeNeeded;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String location;
    private LocalDate expectedStartDate;
    private LocalDate expectedEndDate;
}