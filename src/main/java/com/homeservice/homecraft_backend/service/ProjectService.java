package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ProjectRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProjectSearchRequest;
import com.homeservice.homecraft_backend.model.dto.request.ProjectUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.response.ProjectResponse;
import com.homeservice.homecraft_backend.model.dto.response.UserResponse;
import com.homeservice.homecraft_backend.model.entity.Client;
import com.homeservice.homecraft_backend.model.entity.Project;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.model.enums.ProfessionalType;
import com.homeservice.homecraft_backend.repository.ClientRepository;
import com.homeservice.homecraft_backend.repository.ProjectRepository;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final ProfessionalRepository professionalRepository;
    private final NotificationService notificationService;

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

        project.setStatus("COMPLETED");
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);

        // Send notification to professional if assigned
        if (project.getProfessional() != null) {
            User professional = project.getProfessional().getUser();
            notificationService.notifyProjectCompleted(
                    professional.getId(),
                    professional.getEmail(),
                    professional.getFullName(),
                    project.getTitle(),
                    project.getId()
            );
        }

        return mapToResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    // Search projects with filters
    public Page<ProjectResponse> searchProjects(ProjectSearchRequest request) {
        Specification<Project> spec = (root, query, cb) -> cb.conjunction();

        // Filter by keyword (title, description)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)
                );
            });
        }

        // Filter by project type
        if (request.getProjectType() != null && !request.getProjectType().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("projectType")), request.getProjectType().toLowerCase())
            );
        }

        // Filter by professional type needed
        if (request.getProfessionalTypeNeeded() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("professionalTypeNeeded"), request.getProfessionalTypeNeeded())
            );
        }

        // Filter by location
        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("location")), "%" + request.getLocation().toLowerCase() + "%")
            );
        }

        // Filter by min budget
        if (request.getMinBudget() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("budgetMin"), request.getMinBudget())
            );
        }

        // Filter by max budget
        if (request.getMaxBudget() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("budgetMax"), request.getMaxBudget())
            );
        }

        // Filter by status
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.upper(root.get("status")), request.getStatus().toUpperCase())
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
        Page<Project> projects = projectRepository.findAll(spec, pageable);

        return projects.map(this::mapToResponse);
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