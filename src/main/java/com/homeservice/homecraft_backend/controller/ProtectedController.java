package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/protected")
@RequiredArgsConstructor
public class ProtectedController {

    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(401).body(response);
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        if (!jwtUtil.isTokenValid(token)) {
            response.put("success", false);
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        // Extract user info from token
        String email = jwtUtil.extractUsername(token);
        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("userId", userId);
        data.put("role", role);

        response.put("success", true);
        response.put("message", "Protected endpoint accessed successfully");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}