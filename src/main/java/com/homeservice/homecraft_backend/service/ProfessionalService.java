package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ProfessionalProfileUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProfessionalSearchRequest;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.model.enums.VerificationStatus;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;

    // PUBLIC: Get ALL Approved Professionals
    public List<ProfessionalProfileResponse> getAllProfessionals() {
        return professionalRepository.findAll()
                .stream()
                .filter(p -> p.getVerificationStatus() == VerificationStatus.APPROVED)
                .map(this::mapToResponse)
                .toList();
    }
    // Added for Admin Controller to get ALL (including pending)
    public List<ProfessionalProfileResponse> getAllProfessionalsForAdmin() {
        return professionalRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // PUBLIC: Get Professionals by Type
    public List<ProfessionalProfileResponse> getProfessionalsByType(ProfessionalType type) {
        return professionalRepository.findAll()
                .stream()
                .filter(p -> p.getVerificationStatus() == VerificationStatus.APPROVED)
                .filter(p -> p.getProfessionalType() == type)
                .map(this::mapToResponse)
                .toList();
    }

    // PUBLIC: Get Available Professionals
    public List<ProfessionalProfileResponse> getAvailableProfessionals() {
        return professionalRepository.findAll()
                .stream()
                .filter(p -> p.getVerificationStatus() == VerificationStatus.APPROVED)
                .filter(Professional::isAvailable)
                .map(this::mapToResponse)
                .toList();
    }

    // PUBLIC: Get Professional by ID
    public ProfessionalProfileResponse getProfessionalById(Long id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found"));
        return mapToResponse(professional);
    }

    // PUBLIC: Search Professionals
    public Page<ProfessionalProfileResponse> searchProfessionals(ProfessionalSearchRequest request) {
        List<ProfessionalProfileResponse> results = professionalRepository.findAll()
                .stream()
                .filter(p -> p.getVerificationStatus() == VerificationStatus.APPROVED)
                .filter(p -> request.getProfessionalType() == null || p.getProfessionalType() == request.getProfessionalType())
                .filter(p -> request.getLocation() == null || p.getLocation().toLowerCase().contains(request.getLocation().toLowerCase()))
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(results);
    }

    // PROTECTED: Get My Profile
    public ProfessionalProfileResponse getMyProfile(Long userId) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));
        return mapToResponse(professional);
    }

    // PROTECTED: Update My Profile
    @Transactional
    public ProfessionalProfileResponse updateProfile(Long userId, ProfessionalProfileUpdateRequest request) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        professional.setProfessionalType(request.getProfessionalType());
        professional.setYearsExperience(request.getYearsExperience());
        professional.setBio(request.getBio());
        professional.setLocation(request.getLocation());
        professional.setHourlyRate(request.getHourlyRate());

        User user = professional.getUser();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        professionalRepository.save(professional);
        return mapToResponse(professional);
    }

    // PROTECTED: Update Availability
    @Transactional
    public ProfessionalProfileResponse updateAvailability(Long userId, boolean isAvailable) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        professional.setAvailable(isAvailable);
        professionalRepository.save(professional);
        return mapToResponse(professional);
    }

    // NEW: Update Profile Picture
    @Transactional
    public ProfessionalProfileResponse updateProfilePicture(Long userId, String imageUrl) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        professional.setProfilePicture(imageUrl);
        professionalRepository.save(professional);
        return mapToResponse(professional);
    }

    // Helper method to map Entity to Response DTO
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
        response.setRatingAverage(professional.getRatingAverage());
        response.setTotalReviews(professional.getTotalReviews());
        response.setLicenseNumber(professional.getLicenseNumber());
        response.setVerificationStatus(professional.getVerificationStatus());
        response.setProfilePicture(professional.getProfilePicture());
        response.setAvailable(professional.isAvailable());
        response.setCreatedAt(professional.getCreatedAt());
        return response;
    }
}