package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.request.ClientProfileUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.ClientProfileResponse;
import com.homeservice.homecraft_backend.service.ClientService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientProfileResponse>> getClientById(@PathVariable Long id) {
        ClientProfileResponse client = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success("Client retrieved successfully", client));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClientProfileResponse>> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        ClientProfileResponse profile = clientService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ClientProfileResponse>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ClientProfileUpdateRequest request) {
        Long userId = extractUserId(authHeader);
        ClientProfileResponse updated = clientService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}