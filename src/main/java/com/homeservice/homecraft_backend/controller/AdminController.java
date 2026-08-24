package com.homeservice.homecraft_backend.controller;

import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.model.enums.VerificationStatus;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import com.homeservice.homecraft_backend.service.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ProfessionalService professionalService; // ADD THIS

    // 1. Get ALL professionals mapped to DTO (Clean data, no infinite nesting!)
    @GetMapping("/pending-professionals")
    public ResponseEntity<List<ProfessionalProfileResponse>> getPendingProfessionals() {
        List<ProfessionalProfileResponse> professionals = professionalService.getAllProfessionalsForAdmin();
        return ResponseEntity.ok(professionals);
    }

    // 2. Approve by Professional ID
    @PutMapping("/approve/{professionalId}")
    public ResponseEntity<String> approveProfessional(@PathVariable Long professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        professional.setVerificationStatus(VerificationStatus.APPROVED);
        professionalRepository.save(professional);

        User user = professional.getUser();
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok("Professional approved successfully");
    }

    // 3. Reject by Professional ID
    @PutMapping("/reject/{professionalId}")
    public ResponseEntity<String> rejectProfessional(@PathVariable Long professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        professional.setVerificationStatus(VerificationStatus.REJECTED);
        professionalRepository.save(professional);

        User user = professional.getUser();
        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok("Professional rejected");
    }
}