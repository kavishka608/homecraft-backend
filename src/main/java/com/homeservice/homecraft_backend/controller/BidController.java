package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.request.BidRequest;
import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.BidResponse;
import com.homeservice.homecraft_backend.service.BidService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final JwtUtil jwtUtil;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<BidResponse>> submitBid(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BidRequest request) {
        Long userId = extractUserId(authHeader);
        BidResponse bid = bidService.submitBid(projectId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Bid submitted successfully", bid));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getBidsByProject(@PathVariable Long projectId) {
        List<BidResponse> bids = bidService.getBidsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Bids retrieved successfully", bids));
    }

    @GetMapping("/project/{projectId}/pending")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getPendingBidsByProject(@PathVariable Long projectId) {
        List<BidResponse> bids = bidService.getPendingBidsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Pending bids retrieved successfully", bids));
    }

    @GetMapping("/my-bids")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getMyBids(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        List<BidResponse> bids = bidService.getMyBids(userId);
        return ResponseEntity.ok(ApiResponse.success("Your bids retrieved successfully", bids));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<BidResponse>> acceptBid(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        BidResponse bid = bidService.acceptBid(id);
        return ResponseEntity.ok(ApiResponse.success("Bid accepted successfully", bid));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<BidResponse>> rejectBid(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        BidResponse bid = bidService.rejectBid(id);
        return ResponseEntity.ok(ApiResponse.success("Bid rejected successfully", bid));
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse<BidResponse>> withdrawBid(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        BidResponse bid = bidService.withdrawBid(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Bid withdrawn successfully", bid));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}