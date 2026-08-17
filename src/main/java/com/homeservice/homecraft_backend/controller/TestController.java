package com.homeservice.homecraft_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "HomeCraft Connect API is running!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "SUCCESS");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "homecraft-backend");
        health.put("version", "0.0.1-SNAPSHOT");
        return health;
    }
}