package com.homeservice.homecraft_backend.repository;

import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.model.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    Optional<Professional> findByUserId(Long userId);
    List<Professional> findByProfessionalType(ProfessionalType type);
    List<Professional> findByVerificationStatus(VerificationStatus status);
    List<Professional> findByIsAvailableTrue();
}