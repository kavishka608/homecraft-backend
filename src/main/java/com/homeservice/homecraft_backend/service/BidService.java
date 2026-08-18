package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.BidRequest;
import com.homeservice.homecraft_backend.model.dto.response.BidResponse;
import com.homeservice.homecraft_backend.model.dto.response.ProfessionalProfileResponse;
import com.homeservice.homecraft_backend.model.entity.Bid;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.Project;
import com.homeservice.homecraft_backend.model.enums.BidStatus;
import com.homeservice.homecraft_backend.repository.BidRepository;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final ProfessionalRepository professionalRepository;

    @Transactional
    public BidResponse submitBid(Long projectId, Long userId, BidRequest request) {
        // Get project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if project is open for bids
        if (!project.getStatus().equals("OPEN")) {
            throw new RuntimeException("Project is not open for bids");
        }

        // Get professional
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        // Check if professional already bid on this project
        if (bidRepository.existsByProjectIdAndProfessionalId(projectId, professional.getId())) {
            throw new RuntimeException("You have already submitted a bid for this project");
        }

        // Create bid
        Bid bid = new Bid();
        bid.setProject(project);
        bid.setProfessional(professional);
        bid.setBidAmount(request.getBidAmount());
        bid.setEstimatedDays(request.getEstimatedDays());
        bid.setMessage(request.getMessage());
        bid.setStatus(BidStatus.PENDING);
        bid.setCreatedAt(LocalDateTime.now());
        bid.setUpdatedAt(LocalDateTime.now());

        Bid savedBid = bidRepository.save(bid);
        return mapToResponse(savedBid);
    }

    public List<BidResponse> getBidsByProject(Long projectId) {
        return bidRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BidResponse> getMyBids(Long userId) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));
        return bidRepository.findByProfessionalId(professional.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BidResponse> getPendingBidsByProject(Long projectId) {
        List<Bid> pendingBids = bidRepository.findByProjectIdAndStatus(projectId, BidStatus.PENDING);
        return pendingBids.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BidResponse acceptBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new RuntimeException("This bid cannot be accepted");
        }

        // Update bid status
        bid.setStatus(BidStatus.ACCEPTED);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        // Update project status to IN_PROGRESS and assign professional
        Project project = bid.getProject();
        project.setStatus("IN_PROGRESS");
        project.setProfessional(bid.getProfessional());  // ← ASSIGN PROFESSIONAL TO PROJECT
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);

        // Reject all other pending bids for this project
        List<Bid> pendingBids = bidRepository.findByProjectIdAndStatus(project.getId(), BidStatus.PENDING);
        for (Bid otherBid : pendingBids) {
            if (!otherBid.getId().equals(bidId)) {
                otherBid.setStatus(BidStatus.REJECTED);
                otherBid.setUpdatedAt(LocalDateTime.now());
                bidRepository.save(otherBid);
            }
        }

        return mapToResponse(savedBid);
    }

    @Transactional
    public BidResponse rejectBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new RuntimeException("This bid cannot be rejected");
        }

        bid.setStatus(BidStatus.REJECTED);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        return mapToResponse(savedBid);
    }

    @Transactional
    public BidResponse withdrawBid(Long bidId, Long userId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        // Verify ownership
        if (!bid.getProfessional().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only withdraw your own bids");
        }

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new RuntimeException("This bid cannot be withdrawn");
        }

        bid.setStatus(BidStatus.WITHDRAWN);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        return mapToResponse(savedBid);
    }

    private BidResponse mapToResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setProjectId(bid.getProject().getId());
        response.setProjectTitle(bid.getProject().getTitle());
        response.setBidAmount(bid.getBidAmount());
        response.setEstimatedDays(bid.getEstimatedDays());
        response.setMessage(bid.getMessage());
        response.setStatus(bid.getStatus());
        response.setCreatedAt(bid.getCreatedAt());
        response.setUpdatedAt(bid.getUpdatedAt());

        // Map professional
        Professional professional = bid.getProfessional();
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