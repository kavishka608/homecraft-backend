package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.request.AvailabilityUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProfessionalProfileUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProfessionalSearchRequest;
import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.service.ProfessionalService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/professionals")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;
    private final JwtUtil jwtUtil;

    // Directory for storing profile pictures
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";

    // Public endpoints
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfessionalProfileResponse>>> getAllProfessionals() {
        List<ProfessionalProfileResponse> professionals = professionalService.getAllProfessionals();
        return ResponseEntity.ok(ApiResponse.success("Professionals retrieved successfully", professionals));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ProfessionalProfileResponse>>> getProfessionalsByType(
            @PathVariable ProfessionalType type) {
        List<ProfessionalProfileResponse> professionals = professionalService.getProfessionalsByType(type);
        return ResponseEntity.ok(ApiResponse.success("Professionals retrieved successfully", professionals));
    }


    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ProfessionalProfileResponse>>> getAvailableProfessionals() {
        List<ProfessionalProfileResponse> professionals = professionalService.getAvailableProfessionals();
        return ResponseEntity.ok(ApiResponse.success("Available professionals retrieved successfully", professionals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> getProfessionalById(@PathVariable Long id) {
        ProfessionalProfileResponse professional = professionalService.getProfessionalById(id);
        return ResponseEntity.ok(ApiResponse.success("Professional retrieved successfully", professional));
    }

    // SEARCH PROFESSIONALS ENDPOINT - ADD THIS
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProfessionalProfileResponse>>> searchProfessionals(
            @RequestBody ProfessionalSearchRequest request) {
        Page<ProfessionalProfileResponse> professionals = professionalService.searchProfessionals(request);
        return ResponseEntity.ok(ApiResponse.success("Professionals retrieved successfully", professionals));
    }

    // Protected endpoints
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        ProfessionalProfileResponse profile = professionalService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProfessionalProfileUpdateRequest request) {
        Long userId = extractUserId(authHeader);
        ProfessionalProfileResponse updated = professionalService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    // NEW: Upload Profile Picture
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> uploadProfilePicture(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) throws IOException {

        Long userId = extractUserId(authHeader);

        // Save the file
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath);

        // Create the full URL for the profile picture
        String imageUrl = "http://localhost:8080/uploads/profile-pictures/" + fileName;

        // Update the professional's profile picture in the database
        ProfessionalProfileResponse updated = professionalService.updateProfilePicture(userId, imageUrl);

        return ResponseEntity.ok(ApiResponse.success("Profile picture updated successfully", updated));
    }

    @PutMapping("/availability")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> updateAvailability(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AvailabilityUpdateRequest request) {
        Long userId = extractUserId(authHeader);
        ProfessionalProfileResponse updated = professionalService.updateAvailability(userId, request.isAvailable());
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", updated));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}