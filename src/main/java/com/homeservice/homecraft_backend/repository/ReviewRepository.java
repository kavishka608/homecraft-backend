package com.homeservice.homecraft_backend.repository;

import com.homeservice.homecraft_backend.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProfessionalId(Long professionalId);
    List<Review> findByProjectId(Long projectId);
    List<Review> findByClientId(Long clientId);
    boolean existsByProjectId(Long projectId);
}