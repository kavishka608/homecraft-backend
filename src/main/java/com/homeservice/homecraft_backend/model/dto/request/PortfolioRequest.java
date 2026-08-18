package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PortfolioRequest {
    private String title;
    private String description;
    private String projectType;
    private MultipartFile image;
}