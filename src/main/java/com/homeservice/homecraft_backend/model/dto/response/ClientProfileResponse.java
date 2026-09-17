package com.homeservice.homecraft_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String preferredContactMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}