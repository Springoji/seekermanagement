package com.management.usermanagement.controller;

import com.management.usermanagement.dto.LoginRequest;
import com.management.usermanagement.dto.LoginResponse;
import com.management.usermanagement.dto.RegisterRequest;
import com.management.usermanagement.model.User;
import com.management.usermanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Register - Create a new user account
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new ResponseEntity<>("Email is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                return new ResponseEntity<>("Full name is required", HttpStatus.BAD_REQUEST);
            }
            
            User registeredUser = authService.register(
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
            );
            
            LoginResponse response = new LoginResponse();
            response.setId(registeredUser.getId());
            response.setEmail(registeredUser.getEmail());
            response.setFullName(registeredUser.getFullName());
            response.setRole(registeredUser.getRole());
            response.setMessage("User registered successfully");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Login - Authenticate user with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new ResponseEntity<>("Email is required", HttpStatus.BAD_REQUEST);
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
            }
            
            User authenticatedUser = authService.login(
                request.getEmail(),
                request.getPassword()
            );
            
            LoginResponse response = new LoginResponse();
            response.setId(authenticatedUser.getId());
            response.setEmail(authenticatedUser.getEmail());
            response.setFullName(authenticatedUser.getFullName());
            response.setRole(authenticatedUser.getRole());
            response.setMessage("Login successful");
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Get user profile by ID
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable String userId) {
        try {
            var user = authService.getUserById(userId);
            if (user.isPresent()) {
                LoginResponse response = new LoginResponse();
                response.setId(user.get().getId());
                response.setEmail(user.get().getEmail());
                response.setFullName(user.get().getFullName());
                response.setRole(user.get().getRole());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
