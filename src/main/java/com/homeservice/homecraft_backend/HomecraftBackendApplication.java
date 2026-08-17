package com.homeservice.homecraft_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HomecraftBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(HomecraftBackendApplication.class, args);
		System.out.println("🚀 HomeCraft Connect Backend Started Successfully!");
		System.out.println("📝 API Documentation: http://localhost:8080/api/test");
		System.out.println("💚 Health Check: http://localhost:8080/api/test/health");
	}
}