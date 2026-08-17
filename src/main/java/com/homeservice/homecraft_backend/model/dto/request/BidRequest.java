package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequest {
    private BigDecimal bidAmount;
    private Integer estimatedDays;
    private String message;
}