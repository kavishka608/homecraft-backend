package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer rating;
    private String comment;
}