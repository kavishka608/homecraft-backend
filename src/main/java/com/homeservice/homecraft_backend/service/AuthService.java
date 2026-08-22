package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.LoginRequest;
import com.homeservice.homecraft_backend.model.dto.request.RegisterRequest;
import com.homeservice.homecraft_backend.model.dto.response.AuthResponse;
import com.homeservice.homecraft_backend.model.entity.Client;
import com.homeservice.homecraft_backend.model.entity.Professional;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.model.enums.UserRole;
import com.homeservice.homecraft_backend.repository.ClientRepository;
import com.homeservice.homecraft_backend.repository.ProfessionalRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import com.homeservice.homecraft_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setVerified(false);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Create Professional or Client based on role
        if (request.getRole() == UserRole.PROFESSIONAL) {
            Professional professional = new Professional();
            professional.setUser(savedUser);
            professional.setProfessionalType(request.getProfessionalType());
            professional.setYearsExperience(request.getYearsExperience());
            professional.setBio(request.getBio());
            professional.setLocation(request.getLocation());
            professional.setAvailable(true);
            professional.setCreatedAt(LocalDateTime.now());
            professional.setUpdatedAt(LocalDateTime.now());
            professionalRepository.save(professional);
        } else if (request.getRole() == UserRole.HOMEOWNER) {
            Client client = new Client();
            client.setUser(savedUser);
            client.setAddress(null);
            client.setPreferredContactMethod("EMAIL");
            client.setCreatedAt(LocalDateTime.now());
            client.setUpdatedAt(LocalDateTime.now());
            clientRepository.save(client);
            System.out.println("Client saved with ID: " + client.getId());
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getId(),
                savedUser.getRole().name()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                savedUser.isVerified()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.isVerified()
        );
    }
}