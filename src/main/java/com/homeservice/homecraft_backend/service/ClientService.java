package com.homeservice.homecraft_backend.service;

import com.homeservice.homecraft_backend.model.dto.request.ClientProfileUpdateRequest;
import com.homeservice.homecraft_backend.model.dto.response.ClientProfileResponse;
import com.homeservice.homecraft_backend.model.entity.Client;
import com.homeservice.homecraft_backend.model.entity.User;
import com.homeservice.homecraft_backend.repository.ClientRepository;
import com.homeservice.homecraft_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ClientProfileResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return mapToResponse(client);
    }

    public ClientProfileResponse getMyProfile(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));
        return mapToResponse(client);
    }

    @Transactional
    public ClientProfileResponse updateProfile(Long userId, ClientProfileUpdateRequest request) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        User user = client.getUser();

        // Update User fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Update Client fields
        if (request.getAddress() != null) {
            client.setAddress(request.getAddress());
        }
        if (request.getPreferredContactMethod() != null) {
            client.setPreferredContactMethod(request.getPreferredContactMethod());
        }
        client.setUpdatedAt(LocalDateTime.now());

        Client updatedClient = clientRepository.save(client);
        return mapToResponse(updatedClient);
    }

    private ClientProfileResponse mapToResponse(Client client) {
        ClientProfileResponse response = new ClientProfileResponse();
        response.setId(client.getId());
        response.setEmail(client.getUser().getEmail());
        response.setFullName(client.getUser().getFullName());
        response.setPhone(client.getUser().getPhone());
        response.setAddress(client.getAddress());
        response.setPreferredContactMethod(client.getPreferredContactMethod());
        response.setCreatedAt(client.getCreatedAt());
        response.setUpdatedAt(client.getUpdatedAt());
        return response;
    }
}