package com.management.usermanagement.service;

import com.management.usermanagement.model.User;
import com.management.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Register a new user
     */
    public User register(String email, String password, String fullName) throws Exception {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new Exception("Email already registered");
        }
        
        // Validate email and password
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Password must be at least 6 characters");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash password
        user.setFullName(fullName);
        user.setRole("USER");
        user.setActive(true);
        user.setCreatedAt(System.currentTimeMillis());
        
        return userRepository.save(user);
    }
    
    /**
     * Login user
     */
    public User login(String email, String password) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);
        
        if (user.isEmpty()) {
            throw new Exception("User not found");
        }
        
        User foundUser = user.get();
        
        // Verify password
        if (!passwordEncoder.matches(password, foundUser.getPassword())) {
            throw new Exception("Invalid credentials");
        }
        
        if (!foundUser.isActive()) {
            throw new Exception("User account is inactive");
        }
        
        return foundUser;
    }
    
    /**
     * Find user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
}
