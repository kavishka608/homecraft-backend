package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.request.ProjectRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProjectUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.ProjectResponse;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.service.ProjectService;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import com.homeservice.homecraft_backend.model.dto.request.ProjectSearchRequest;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtUtil jwtUtil;

    // Public endpoints
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getOpenProjects() {
        List<ProjectResponse> projects = projectService.getOpenProjects();
        return ResponseEntity.ok(ApiResponse.success("Open projects retrieved successfully", projects));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByType(@PathVariable ProfessionalType type) {
        List<ProjectResponse> projects = projectService.getProjectsByType(type);
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
    }

    @GetMapping("/open/type/{type}")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getOpenProjectsByType(@PathVariable ProfessionalType type) {
        List<ProjectResponse> projects = projectService.getOpenProjectsByType(type);
        return ResponseEntity.ok(ApiResponse.success("Open projects retrieved successfully", projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success("Project retrieved successfully", project));
    }

    // Protected endpoints (JWT required)
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProjectRequest request) {
        Long userId = extractUserId(authHeader);
        ProjectResponse project = projectService.createProject(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Project created successfully", project));
    }

    @GetMapping("/my-projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        List<ProjectResponse> projects = projectService.getProjectsByClient(userId);
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProjectUpdateRequest request) {
        ProjectResponse project = projectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", project));
    }

    // COMPLETE PROJECT ENDPOINT
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ProjectResponse>> completeProject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        ProjectResponse project = projectService.completeProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project completed successfully", project));
    }

    // NEW: Admin Approve Project Endpoint
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ProjectResponse>> approveProject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        ProjectResponse project = projectService.approveProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project approved successfully", project));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> searchProjects(
            @RequestBody ProjectSearchRequest request) {
        Page<ProjectResponse> projects = projectService.searchProjects(request);
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", projects));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}