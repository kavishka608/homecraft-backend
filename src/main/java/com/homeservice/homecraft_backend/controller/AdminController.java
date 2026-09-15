package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.response.ApiResponse;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.dto.response.ProjectResponse;
import com.homeservice.homecraft_backend.service.ProfessionalService;
import com.homeservice.homecraft_backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminController {

    private final ProfessionalService professionalService;
    private final ProjectService projectService;

    // ============ PROFESSIONALS ============

    @GetMapping("/pending-professionals")
    public ResponseEntity<ApiResponse<List<ProfessionalProfileResponse>>> getPendingProfessionals() {
        List<ProfessionalProfileResponse> professionals = professionalService.getPendingProfessionals();
        return ResponseEntity.ok(ApiResponse.success("Pending professionals retrieved", professionals));
    }

    @PutMapping("/approve/{professionalId}")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> approveProfessional(
            @PathVariable Long professionalId) {
        ProfessionalProfileResponse professional = professionalService.approveProfessional(professionalId);
        return ResponseEntity.ok(ApiResponse.success("Professional approved successfully", professional));
    }

    @PutMapping("/reject/{professionalId}")
    public ResponseEntity<ApiResponse<ProfessionalProfileResponse>> rejectProfessional(
            @PathVariable Long professionalId) {
        ProfessionalProfileResponse professional = professionalService.rejectProfessional(professionalId);
        return ResponseEntity.ok(ApiResponse.success("Professional rejected successfully", professional));
    }

    // ============ PROJECTS ============

    @GetMapping("/pending-projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getPendingProjects() {
        List<ProjectResponse> projects = projectService.getPendingProjects();
        return ResponseEntity.ok(ApiResponse.success("Pending projects retrieved", projects));
    }

    @PutMapping("/projects/{projectId}/approve")
    public ResponseEntity<ApiResponse<ProjectResponse>> approveProject(@PathVariable Long projectId) {
        ProjectResponse project = projectService.approveProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project approved successfully", project));
    }

    @PutMapping("/projects/{projectId}/reject")
    public ResponseEntity<ApiResponse<ProjectResponse>> rejectProject(@PathVariable Long projectId) {
        ProjectResponse project = projectService.rejectProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project rejected successfully", project));
    }
}