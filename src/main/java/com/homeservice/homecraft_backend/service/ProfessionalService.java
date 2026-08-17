package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ProfessionalProfileUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;  // ← ADD THIS
import com.homeservice.homecraft_backend.model.dto.response.PortfolioItemResponse;       // ← ADD THIS
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;

    public ProfessionalProfileResponse getProfessionalById(Long id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found"));
        return mapToResponse(professional);
    }

    public List<ProfessionalProfileResponse> getAllProfessionals() {
        return professionalRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProfessionalProfileResponse> getProfessionalsByType(ProfessionalType type) {
        return professionalRepository.findByProfessionalType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProfessionalProfileResponse> getAvailableProfessionals() {
        return professionalRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProfessionalProfileResponse getMyProfile(Long userId) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));
        return mapToResponse(professional);
    }

    @Transactional
    public ProfessionalProfileResponse updateProfile(Long userId, ProfessionalProfileUpdateRequest request) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        User user = professional.getUser();

        // Update User fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Update Professional fields
        if (request.getProfessionalType() != null) {
            professional.setProfessionalType(request.getProfessionalType());
        }
        if (request.getYearsExperience() != null) {
            professional.setYearsExperience(request.getYearsExperience());
        }
        if (request.getHourlyRate() != null) {
            professional.setHourlyRate(request.getHourlyRate());
        }
        if (request.getBio() != null) {
            professional.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            professional.setLocation(request.getLocation());
        }
        if (request.getLicenseNumber() != null) {
            professional.setLicenseNumber(request.getLicenseNumber());
        }
        professional.setUpdatedAt(LocalDateTime.now());

        Professional updatedProfessional = professionalRepository.save(professional);
        return mapToResponse(updatedProfessional);
    }

    @Transactional
    public ProfessionalProfileResponse updateAvailability(Long userId, boolean available) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        professional.setAvailable(available);
        professional.setUpdatedAt(LocalDateTime.now());

        Professional updatedProfessional = professionalRepository.save(professional);
        return mapToResponse(updatedProfessional);
    }

    private ProfessionalProfileResponse mapToResponse(Professional professional) {
        ProfessionalProfileResponse response = new ProfessionalProfileResponse();
        response.setId(professional.getId());
        response.setEmail(professional.getUser().getEmail());
        response.setFullName(professional.getUser().getFullName());
        response.setPhone(professional.getUser().getPhone());
        response.setProfessionalType(professional.getProfessionalType());
        response.setYearsExperience(professional.getYearsExperience());
        response.setHourlyRate(professional.getHourlyRate());
        response.setBio(professional.getBio());
        response.setLocation(professional.getLocation());
        response.setAvailable(professional.isAvailable());
        response.setRatingAverage(professional.getRatingAverage());
        response.setTotalReviews(professional.getTotalReviews());
        response.setLicenseNumber(professional.getLicenseNumber());
        response.setVerificationStatus(professional.getVerificationStatus());
        response.setProfilePicture(professional.getProfilePicture());
        response.setCreatedAt(professional.getCreatedAt());
        return response;
    }
}