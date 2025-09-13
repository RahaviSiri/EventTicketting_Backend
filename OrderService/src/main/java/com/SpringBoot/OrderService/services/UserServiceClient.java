package com.SpringBoot.OrderService.services;

import com.SpringBoot.OrderService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "UserService", url = "http://localhost:8087") 
public interface UserServiceClient {

    @GetMapping("/api/users/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}

