package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.request.PortfolioRequest;
import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.PortfolioItemResponse;
import com.homeservice.homecraft_backend.service.PortfolioService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final JwtUtil jwtUtil;

    // Add portfolio item (Professional only)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PortfolioItemResponse>> addPortfolioItem(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute PortfolioRequest request) {
        Long userId = extractUserId(authHeader);
        PortfolioItemResponse item = portfolioService.addPortfolioItem(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Portfolio item added successfully", item));
    }

    // Get portfolio by professional ID (Public)
    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<ApiResponse<List<PortfolioItemResponse>>> getPortfolioByProfessional(
            @PathVariable Long professionalId) {
        List<PortfolioItemResponse> items = portfolioService.getPortfolioByProfessional(professionalId);
        return ResponseEntity.ok(ApiResponse.success("Portfolio retrieved successfully", items));
    }

    // Get my portfolio (Professional only)
    @GetMapping("/my-portfolio")
    public ResponseEntity<ApiResponse<List<PortfolioItemResponse>>> getMyPortfolio(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        List<PortfolioItemResponse> items = portfolioService.getMyPortfolio(userId);
        return ResponseEntity.ok(ApiResponse.success("Your portfolio retrieved successfully", items));
    }

    // Update portfolio item (Professional only)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PortfolioItemResponse>> updatePortfolioItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute PortfolioRequest request) {
        Long userId = extractUserId(authHeader);
        PortfolioItemResponse item = portfolioService.updatePortfolioItem(id, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Portfolio item updated successfully", item));
    }

    // Delete portfolio item (Professional only)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePortfolioItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        portfolioService.deletePortfolioItem(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Portfolio item deleted successfully", null));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}