package com.management.usermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    /**
     * Root endpoint - Welcome message
     */
    @GetMapping("/")
    public ResponseEntity<?> welcome() {
        return new ResponseEntity<>(
            "Welcome to User Management API! Use /api/customers for customer operations.",
            HttpStatus.OK
        );
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return new ResponseEntity<>(
            "Application is running successfully!",
            HttpStatus.OK
        );
    }
}
