package com.homeservice.homecraft_backend.repository;

import com.homeservice.homecraft_backend.model.entity.Project;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByClientId(Long clientId);
    List<Project> findByStatus(String status);
    List<Project> findByProfessionalTypeNeeded(ProfessionalType professionalType);
    List<Project> findByStatusAndProfessionalTypeNeeded(String status, ProfessionalType professionalType);
}