package com.SpringBoot.UserService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringBoot.UserService.dto.UserDTO;
import com.SpringBoot.UserService.model.User;
import com.SpringBoot.UserService.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserNotification UserNotification;

    public void registerUser(User user) {
        // Save user
        userRepository.save(user);

        // Publish welcome email
        try {
            String payload = String.format(
                    "{ \"email\": \"%s\", \"name\": \"%s\" }",
                    user.getEmail(),
                    user.getName());

            UserNotification.publishUserCreated(payload);
            System.out.println("Published welcome email for userId=" + user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserRole(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getRole();
        }
        return "No user Found";
    }

    public void setUserRole(String email, String role) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("User found, updating role to " + role);
            user.get().setRole(role);
            userRepository.save(user.get());
        } else {
            System.out.println("User not found for email: " + email);
        }
    }

    public Long getUserID(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println(user.get().getId());
            return user.get().getId();
        }
        return null;
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .orElse(null);
    }

    public void changePassword(String email, String hashedPassword) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPasswordHash(hashedPassword);
            userRepository.save(user.get());
        } else {
            System.out.println("User not found for email: " + email);
        }
    }
}
