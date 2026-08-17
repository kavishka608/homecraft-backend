package com.homeservice.homecraft_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String projectType;
    private LocalDateTime createdAt;
}