package com.SpringBoot.UserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringBoot.UserService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

