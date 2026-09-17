package com.homeservice.homecraft_backend.model.dto.response;

import com.homeservice.homecraft_backend.model.enums.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private ProfessionalProfileResponse professional;
    private BigDecimal bidAmount;
    private Integer estimatedDays;
    private String message;
    private BidStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}