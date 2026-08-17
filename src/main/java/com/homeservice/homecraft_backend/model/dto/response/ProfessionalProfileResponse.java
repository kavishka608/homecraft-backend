package com.homeservice.homecraft_backend.model.dto.response;

import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.model.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private ProfessionalType professionalType;
    private Integer yearsExperience;
    private BigDecimal hourlyRate;
    private String bio;
    private String location;
    private boolean isAvailable;
    private Double ratingAverage;
    private Integer totalReviews;
    private String licenseNumber;
    private VerificationStatus verificationStatus;
    private String profilePicture;
    private LocalDateTime createdAt;
    private List<PortfolioItemResponse> portfolioItems;
}