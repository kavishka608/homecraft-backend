package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.request.ReviewRequest;
import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.ReviewResponse;
import com.homeservice.homecraft_backend.service.ReviewService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    // Add review (Protected - Client only)
    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReviewRequest request) {
        Long userId = extractUserId(authHeader);
        ReviewResponse review = reviewService.addReview(projectId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Review added successfully", review));
    }

    // Get reviews by professional (Public)
    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByProfessional(
            @PathVariable Long professionalId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProfessional(professionalId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }

    // Get reviews by project (Public)
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByProject(
            @PathVariable Long projectId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }

    // Get my reviews (Protected - Client)
    @GetMapping("/my-reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        List<ReviewResponse> reviews = reviewService.getReviewsByClient(userId);
        return ResponseEntity.ok(ApiResponse.success("Your reviews retrieved successfully", reviews));
    }

    // Delete review (Protected - Client only)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        reviewService.deleteReview(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}