package com.SpringBoot.UserService.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringBoot.UserService.model.User;
import com.SpringBoot.UserService.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("User service is reachable ✅");
    }

    @Autowired
    private UserRepository userRepository;

    
    
@PostMapping("/register")
public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
    // Encode raw password from frontend
    user.setPasswordHash(new BCryptPasswordEncoder().encode(user.getPassword()));

    // Optional: default role
    if(user.getRole() == null) user.setRole("USER");

    // Save to DB
    userRepository.save(user);

    // Return JSON instead of plain string
    return ResponseEntity.ok(Map.of("message", "User registered successfully"));
}


    

    // @PostMapping("/register")
    // public ResponseEntity<String> register(@RequestBody User user) {
    //     // You should encode password before saving
    //     user.setPasswordHash(new BCryptPasswordEncoder().encode(user.getPasswordHash()));
    //     userRepository.save(user);
    //     return ResponseEntity.ok("User registered successfully");
    // }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent() && 
           new BCryptPasswordEncoder().matches(password, userOpt.get().getPasswordHash())) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}