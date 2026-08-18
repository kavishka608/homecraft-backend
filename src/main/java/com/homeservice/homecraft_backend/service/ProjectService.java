package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ProjectRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProjectUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.response.ProjectResponse;
import com.homeservice.homecraft_backend.model.dto.response.UserResponse;
import com.homeservice.homecraft_backend.model.entity.Client;
import com.homeservice.homecraft_backend.model.entity.Project;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.repository.ClientRepository;
import com.homeservice.homecraft_backend.repository.ProjectRepository;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final ProfessionalRepository professionalRepository;

    @Transactional
    public ProjectResponse createProject(Long userId, ProjectRequest request) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Project project = new Project();
        project.setClient(client);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setProjectType(request.getProjectType());
        project.setProfessionalTypeNeeded(request.getProfessionalTypeNeeded());
        project.setBudgetMin(request.getBudgetMin());
        project.setBudgetMax(request.getBudgetMax());
        project.setLocation(request.getLocation());
        project.setExpectedStartDate(request.getExpectedStartDate());
        project.setExpectedEndDate(request.getExpectedEndDate());
        project.setStatus("OPEN");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getOpenProjects() {
        return projectRepository.findByStatus("OPEN").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByType(ProfessionalType type) {
        return projectRepository.findByProfessionalTypeNeeded(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getOpenProjectsByType(ProfessionalType type) {
        return projectRepository.findByStatusAndProfessionalTypeNeeded("OPEN", type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToResponse(project);
    }

    public List<ProjectResponse> getProjectsByClient(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return projectRepository.findByClientId(client.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getBudgetMin() != null) {
            project.setBudgetMin(request.getBudgetMin());
        }
        if (request.getBudgetMax() != null) {
            project.setBudgetMax(request.getBudgetMax());
        }
        if (request.getLocation() != null) {
            project.setLocation(request.getLocation());
        }
        if (request.getExpectedStartDate() != null) {
            project.setExpectedStartDate(request.getExpectedStartDate());
        }
        if (request.getExpectedEndDate() != null) {
            project.setExpectedEndDate(request.getExpectedEndDate());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    @Transactional
    public ProjectResponse completeProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Professional check removed - project can be completed without a professional assigned
        // This allows testing of the review system

        project.setStatus("COMPLETED");
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setProjectType(project.getProjectType());
        response.setProfessionalTypeNeeded(project.getProfessionalTypeNeeded());
        response.setBudgetMin(project.getBudgetMin());
        response.setBudgetMax(project.getBudgetMax());
        response.setLocation(project.getLocation());
        response.setExpectedStartDate(project.getExpectedStartDate());
        response.setExpectedEndDate(project.getExpectedEndDate());
        response.setStatus(project.getStatus());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        // Map client to UserResponse
        Client client = project.getClient();
        UserResponse clientResponse = new UserResponse();
        clientResponse.setId(client.getId());
        clientResponse.setEmail(client.getUser().getEmail());
        clientResponse.setFullName(client.getUser().getFullName());
        clientResponse.setPhone(client.getUser().getPhone());
        response.setClient(clientResponse);

        return response;
    }
}