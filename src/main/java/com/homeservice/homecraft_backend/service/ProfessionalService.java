package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ProfessionalProfileUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProfessionalSearchRequest;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    // Existing methods
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

    // NEW: Search professionals with filters
    public Page<ProfessionalProfileResponse> searchProfessionals(ProfessionalSearchRequest request) {
        Specification<Professional> spec = (root, query, cb) -> cb.conjunction(); // Start with true condition

        // Filter by keyword (name, bio, location)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("user").get("fullName")), keyword),
                        cb.like(cb.lower(root.get("bio")), keyword),
                        cb.like(cb.lower(root.get("location")), keyword)
                );
            });
        }

        // Filter by professional type
        if (request.getProfessionalType() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("professionalType"), request.getProfessionalType())
            );
        }

        // Filter by location
        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("location")), "%" + request.getLocation().toLowerCase() + "%")
            );
        }

        // Filter by min rating
        if (request.getMinRating() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("ratingAverage"), request.getMinRating())
            );
        }

        // Filter by max rating
        if (request.getMaxRating() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("ratingAverage"), request.getMaxRating())
            );
        }

        // Filter by min hourly rate
        if (request.getMinHourlyRate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("hourlyRate"), request.getMinHourlyRate())
            );
        }

        // Filter by max hourly rate
        if (request.getMaxHourlyRate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("hourlyRate"), request.getMaxHourlyRate())
            );
        }

        // Filter by availability
        if (request.getIsAvailable() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isAvailable"), request.getIsAvailable())
            );
        }

        // Sort
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getSortBy());
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Professional> professionals = professionalRepository.findAll(spec, pageable);

        return professionals.map(this::mapToResponse);
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

        // Map portfolio items
        if (professional.getPortfolioItems() != null && !professional.getPortfolioItems().isEmpty()) {
            response.setPortfolioItems(
                    professional.getPortfolioItems().stream()
                            .map(item -> {
                                com.homeservice.homecraft_backend.model.dto.response.PortfolioItemResponse portfolioResponse =
                                        new com.homeservice.homecraft_backend.model.dto.response.PortfolioItemResponse();
                                portfolioResponse.setId(item.getId());
                                portfolioResponse.setTitle(item.getTitle());
                                portfolioResponse.setDescription(item.getDescription());
                                portfolioResponse.setImageUrl(item.getImageUrl());
                                portfolioResponse.setProjectType(item.getProjectType());
                                portfolioResponse.setCreatedAt(item.getCreatedAt());
                                return portfolioResponse;
                            })
                            .collect(Collectors.toList())
            );
        }

        return response;
    }
}