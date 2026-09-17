package com.homeservice.homecraft_backend.model.dto.request;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfessionalProfileUpdateRequest {
    private String fullName;
    private String phone;
    private ProfessionalType professionalType;
    private Integer yearsExperience;
    private BigDecimal hourlyRate;
    private String bio;
    private String location;
    private String licenseNumber;
}