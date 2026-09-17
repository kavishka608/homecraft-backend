package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ReviewRequest;
import com.homeservice.homecraft_backend.model.dto.response.ClientProfileResponse;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.dto.response.ReviewResponse;
import com.homeservice.homecraft_backend.model.entity.Client;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.Project;
import com.homeservice.homecraft_backend.model.entity.Review;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.repository.ClientRepository;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.ProjectRepository;
import com.homeservice.homecraft_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final ProfessionalRepository professionalRepository;
    private final NotificationService notificationService;

    @Transactional
    public ReviewResponse addReview(Long projectId, Long userId, ReviewRequest request) {
        // Get project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if project is completed
        if (!project.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Project must be completed before leaving a review");
        }

        // Check if review already exists for this project
        if (reviewRepository.existsByProjectId(projectId)) {
            throw new RuntimeException("A review already exists for this project");
        }

        // Get client
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Verify client owns the project
        if (!project.getClient().getId().equals(client.getId())) {
            throw new RuntimeException("You can only review projects you created");
        }

        // Get professional - if none assigned, find any professional in the system
        Professional professional = project.getProfessional();
        if (professional == null) {
            // Find the first available professional (for testing purposes)
            professional = professionalRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No professional found in system"));
        }

        // Create review
        Review review = new Review();
        review.setProject(project);
        review.setClient(client);
        review.setProfessional(professional);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // Update professional's average rating
        updateProfessionalRating(professional.getId());

        // Send notification to professional about new review
        User professionalUser = professional.getUser();
        notificationService.notifyNewReview(
                professionalUser.getId(),
                professionalUser.getEmail(),
                professionalUser.getFullName(),
                project.getTitle(),
                request.getRating(),
                savedReview.getId()
        );

        return mapToResponse(savedReview);
    }

    public List<ReviewResponse> getReviewsByProfessional(Long professionalId) {
        return reviewRepository.findByProfessionalId(professionalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByProject(Long projectId) {
        return reviewRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByClient(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return reviewRepository.findByClientId(client.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateProfessionalRating(Long professionalId) {
        List<Review> reviews = reviewRepository.findByProfessionalId(professionalId);

        if (reviews.isEmpty()) {
            return;
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        professional.setRatingAverage(averageRating);
        professional.setTotalReviews(reviews.size());
        professional.setUpdatedAt(LocalDateTime.now());
        professionalRepository.save(professional);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Verify client owns the review
        if (!review.getClient().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        Long professionalId = review.getProfessional().getId();
        reviewRepository.delete(review);

        // Update professional's average rating
        updateProfessionalRating(professionalId);
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProjectId(review.getProject().getId());
        response.setProjectTitle(review.getProject().getTitle());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());

        // Map client
        Client client = review.getClient();
        ClientProfileResponse clientResponse = new ClientProfileResponse();
        clientResponse.setId(client.getId());
        clientResponse.setEmail(client.getUser().getEmail());
        clientResponse.setFullName(client.getUser().getFullName());
        clientResponse.setPhone(client.getUser().getPhone());
        clientResponse.setAddress(client.getAddress());
        clientResponse.setPreferredContactMethod(client.getPreferredContactMethod());
        response.setClient(clientResponse);

        // Map professional
        Professional professional = review.getProfessional();
        ProfessionalProfileResponse professionalResponse = new ProfessionalProfileResponse();
        professionalResponse.setId(professional.getId());
        professionalResponse.setEmail(professional.getUser().getEmail());
        professionalResponse.setFullName(professional.getUser().getFullName());
        professionalResponse.setPhone(professional.getUser().getPhone());
        professionalResponse.setProfessionalType(professional.getProfessionalType());
        professionalResponse.setYearsExperience(professional.getYearsExperience());
        professionalResponse.setHourlyRate(professional.getHourlyRate());
        professionalResponse.setBio(professional.getBio());
        professionalResponse.setLocation(professional.getLocation());
        professionalResponse.setAvailable(professional.isAvailable());
        professionalResponse.setRatingAverage(professional.getRatingAverage());
        professionalResponse.setTotalReviews(professional.getTotalReviews());
        professionalResponse.setVerificationStatus(professional.getVerificationStatus());
        response.setProfessional(professionalResponse);

        return response;
    }
}