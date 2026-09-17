package com.homeservice.homecraft_backend.model.dto.request;

import lombok.Data;

@Data
public class ClientProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String address;
    private String preferredContactMethod;
}