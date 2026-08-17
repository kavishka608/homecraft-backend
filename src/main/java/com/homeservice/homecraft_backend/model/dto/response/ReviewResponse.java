package com.homeservice.homecraft_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long projectId;
    private String projectTitle;
    private UserResponse reviewer;
    private ProfessionalResponse professional;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}