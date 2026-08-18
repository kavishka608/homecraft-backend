package com.homeservice.homecraft_backend.repository;

import com.homeservice.homecraft_backend.model.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findByProfessionalId(Long professionalId);
    void deleteByProfessionalId(Long professionalId);
}