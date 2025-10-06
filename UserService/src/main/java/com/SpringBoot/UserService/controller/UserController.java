package com.SpringBoot.UserService.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringBoot.UserService.dto.UserDTO;
import com.SpringBoot.UserService.model.User;
import com.SpringBoot.UserService.repository.UserRepository;
import com.SpringBoot.UserService.services.UserService;
import com.SpringBoot.UserService.utils.JwtUtils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody HashMap<String, String> entity) {
        String name = entity.get("name");
        String email = entity.get("email");
        String password = entity.get("password");
        String hashedPassword = passwordEncoder.encode(password);
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(400).body("Email already in use");
        }
        userService.registerUser(User.builder().name(name).email(email).passwordHash(hashedPassword).role("ATTENDEE")
                .createdAt(LocalDateTime.now()).build());
        String token = jwtUtils.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token, "message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> LoginUser(@RequestBody HashMap<String, String> entity) {
        String email = entity.get("email");
        String password = entity.get("password");
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid email");
        }
        if (!passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return ResponseEntity.status(400).body("Invalid password");
        }
        String token = jwtUtils.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/getRole")
    public ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String token) {
        String email = jwtUtils.extractEmail(token.replace("Bearer ", "").trim());
        String role = userService.getUserRole(email);
        return ResponseEntity.ok(Map.of("role", role));
    }

    @PostMapping("/setRole")
    public ResponseEntity<?> setUserRole(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        String email = jwtUtils.extractEmail(token.replace("Bearer ", "").trim());
        String role = body.get("role");
        userService.setUserRole(email, role);
        return ResponseEntity.ok(Map.of("message", "Role updated"));
    }

    @GetMapping("/getUserID")
    public ResponseEntity<?> getUserID(@RequestHeader("Authorization") String token) {
        String email = jwtUtils.extractEmail(token.replace("Bearer ", "").trim());
        Long userID = userService.getUserID(email);
        return ResponseEntity.ok(Map.of("userID", userID));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }

    // @PostMapping("/verify-email")
    // public ResponseEntity<?> verifyEmail(@RequestBody String email) {
    // boolean exists = userRepository.findByEmail(email).isPresent();
    // if (exists) {
    // return ResponseEntity.ok("Email exists");
    // } else {
    // return ResponseEntity.status(400).body("Email is not available");
    // }
    // }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean exists = userRepository.findByEmail(email).isPresent();

        if (exists) {
            return ResponseEntity.ok(Map.of("message", "Email exists"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "Email not found"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");
        String hashedPassword = passwordEncoder.encode(newPassword);
        userService.changePassword(email, hashedPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
