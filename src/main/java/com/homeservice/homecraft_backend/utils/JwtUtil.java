package com.homeservice.homecraft_backend.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "homecraft-secret-key-for-jwt-12345678901234567890123456789012";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public String generateToken(String email, Long userId, String role) {
        try {
            // Create header (Base64 URL encoded)
            String header = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8)
            );

            // Create payload as JSON string
            long now = System.currentTimeMillis();
            long exp = now + EXPIRATION_TIME;

            String payloadJson = String.format(
                    "{\"sub\":\"%s\",\"userId\":%d,\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                    email, userId, role, now / 1000, exp / 1000
            );

            // Base64 URL encode the payload
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                    payloadJson.getBytes(StandardCharsets.UTF_8)
            );

            // Create signature
            String signatureInput = header + "." + payload;
            String signature = generateSignature(signatureInput);

            // Return complete JWT
            return header + "." + payload + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token: " + e.getMessage(), e);
        }
    }

    private String generateSignature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((data + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    private String[] splitToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token is null or empty");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT token format. Expected 3 parts, got " + parts.length);
        }
        return parts;
    }

    private String decodeBase64Url(String encoded) {
        try {
            // Add padding if needed
            String base64 = encoded;
            int padding = 4 - (base64.length() % 4);
            if (padding < 4) {
                base64 += "=".repeat(padding);
            }
            return new String(Base64.getUrlDecoder().decode(base64), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding Base64: " + e.getMessage(), e);
        }
    }

    public String extractUsername(String token) {
        try {
            String[] parts = splitToken(token);
            String payload = decodeBase64Url(parts[1]);
            return extractValueFromJson(payload, "sub");
        } catch (Exception e) {
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            String[] parts = splitToken(token);
            String payload = decodeBase64Url(parts[1]);
            String userIdStr = extractValueFromJson(payload, "userId");
            return userIdStr != null ? Long.parseLong(userIdStr) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            String[] parts = splitToken(token);
            String payload = decodeBase64Url(parts[1]);
            return extractValueFromJson(payload, "role");
        } catch (Exception e) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            String[] parts = splitToken(token);
            String payload = decodeBase64Url(parts[1]);
            String expStr = extractValueFromJson(payload, "exp");
            if (expStr != null) {
                long expTimestamp = Long.parseLong(expStr) * 1000;
                return new Date(expTimestamp);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractValueFromJson(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }
        if (endIndex == -1) {
            return null;
        }
        String value = json.substring(startIndex, endIndex).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    public Boolean isTokenValid(String token) {
        try {
            if (token == null || token.isEmpty()) {
                System.out.println("Token is null or empty");
                return false;
            }

            String[] parts = splitToken(token);

            // Verify signature
            String signatureInput = parts[0] + "." + parts[1];
            String expectedSignature = generateSignature(signatureInput);

            if (!expectedSignature.equals(parts[2])) {
                System.out.println("Signature mismatch. Expected: " + expectedSignature + ", Got: " + parts[2]);
                return false;
            }

            // Check expiration
            Date expiration = extractExpiration(token);
            if (expiration != null) {
                boolean isExpired = expiration.before(new Date());
                if (isExpired) {
                    System.out.println("Token expired at: " + expiration);
                }
                return !isExpired;
            }
            return false;

        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    public Boolean validateToken(String token, String email) {
        try {
            if (!isTokenValid(token)) {
                return false;
            }

            String username = extractUsername(token);
            return username != null && username.equals(email);

        } catch (Exception e) {
            return false;
        }
    }
}