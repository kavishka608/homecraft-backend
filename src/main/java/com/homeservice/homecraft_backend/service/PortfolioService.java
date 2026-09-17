package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.PortfolioRequest;
import com.homeservice.homecraft_backend.model.dto.response.PortfolioItemResponse;
import com.homeservice.homecraft_backend.model.entity.PortfolioItem;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.repository.PortfolioRepository;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ProfessionalRepository professionalRepository;

    private static final String UPLOAD_DIR = "uploads/portfolio/";

    @Transactional
    public PortfolioItemResponse addPortfolioItem(Long userId, PortfolioRequest request) {
        // Get professional
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));

        // Save image
        String imageUrl = saveImage(request.getImage());

        // Create portfolio item
        PortfolioItem item = new PortfolioItem();
        item.setProfessional(professional);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setProjectType(request.getProjectType());
        item.setImageUrl(imageUrl);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        PortfolioItem savedItem = portfolioRepository.save(item);
        return mapToResponse(savedItem);
    }

    public List<PortfolioItemResponse> getPortfolioByProfessional(Long professionalId) {
        return portfolioRepository.findByProfessionalId(professionalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PortfolioItemResponse> getMyPortfolio(Long userId) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional profile not found"));
        return portfolioRepository.findByProfessionalId(professional.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioItemResponse updatePortfolioItem(Long itemId, Long userId, PortfolioRequest request) {
        PortfolioItem item = portfolioRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Portfolio item not found"));

        // Verify ownership
        if (!item.getProfessional().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own portfolio items");
        }

        if (request.getTitle() != null) {
            item.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getProjectType() != null) {
            item.setProjectType(request.getProjectType());
        }
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            // Delete old image
            deleteImage(item.getImageUrl());
            // Save new image
            String imageUrl = saveImage(request.getImage());
            item.setImageUrl(imageUrl);
        }
        item.setUpdatedAt(LocalDateTime.now());

        PortfolioItem updatedItem = portfolioRepository.save(item);
        return mapToResponse(updatedItem);
    }

    @Transactional
    public void deletePortfolioItem(Long itemId, Long userId) {
        PortfolioItem item = portfolioRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Portfolio item not found"));

        // Verify ownership
        if (!item.getProfessional().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own portfolio items");
        }

        // Delete image
        deleteImage(item.getImageUrl());
        portfolioRepository.delete(item);
    }

    private String saveImage(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/portfolio/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }

    private void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // Log error but don't throw
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }

    private PortfolioItemResponse mapToResponse(PortfolioItem item) {
        PortfolioItemResponse response = new PortfolioItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setImageUrl(item.getImageUrl());
        response.setProjectType(item.getProjectType());
        response.setCreatedAt(item.getCreatedAt());
        return response;
    }
}