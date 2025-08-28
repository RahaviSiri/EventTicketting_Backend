package com.SpringBoot.UserService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringBoot.UserService.model.User;
import com.SpringBoot.UserService.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void registerUser(User user) {
        userRepository.save(user);
    }
    
    public String getUserRole(String email) {
        var user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return user.get().getRole();
        }
        return "No user Found";
    }

    public void setUserRole(String email, String role) {
        var user = userRepository.findByEmail(email);
        if(user.isPresent()){
            System.out.println("User found, updating role to " + role);
            user.get().setRole(role);
            userRepository.save(user.get());
        } else {
            System.out.println("User not found for email: " + email);
        }
    }
}
