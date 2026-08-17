package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}