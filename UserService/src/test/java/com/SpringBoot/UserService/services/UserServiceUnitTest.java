package com.SpringBoot.UserService.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.SpringBoot.UserService.model.User;
import com.SpringBoot.UserService.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserNotification UserNotification;

    @InjectMocks
    UserService userService;

    @Test
    void getUserRole_returnsRoleWhenPresent() {
        User u = new User();
        u.setId(2L);
        u.setEmail("u@example.com");
        u.setRole("ATTENDEE");

        when(userRepository.findByEmail("u@example.com")).thenReturn(Optional.of(u));

        String role = userService.getUserRole("u@example.com");
        assertThat(role).isEqualTo("ATTENDEE");
    }
}
