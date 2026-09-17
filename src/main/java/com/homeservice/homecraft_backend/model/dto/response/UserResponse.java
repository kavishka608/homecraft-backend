package com.homeservice.homecraft_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private boolean isVerified;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}