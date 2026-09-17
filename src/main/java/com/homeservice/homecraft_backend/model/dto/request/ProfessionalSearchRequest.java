package com.homeservice.homecraft_backend.model.dto.request;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfessionalSearchRequest {
    private String keyword;
    private ProfessionalType professionalType;
    private String location;
    private Double minRating;
    private Double maxRating;
    private BigDecimal minHourlyRate;
    private BigDecimal maxHourlyRate;
    private Boolean isAvailable;
    private String sortBy;
    private String sortDirection;
    private Integer page = 0;
    private Integer size = 10;
}