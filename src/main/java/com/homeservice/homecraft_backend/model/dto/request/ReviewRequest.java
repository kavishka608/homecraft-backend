package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer rating;  // 1-5
    private String comment;
}