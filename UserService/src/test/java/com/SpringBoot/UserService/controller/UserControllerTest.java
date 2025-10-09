package com.SpringBoot.UserService.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.SpringBoot.UserService.services.UserService;
import com.SpringBoot.UserService.repository.UserRepository;
import com.SpringBoot.UserService.utils.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void register_returnsOk() throws Exception {
        HashMap<String,String> body = new HashMap<>();
        body.put("name","A");
        body.put("email","a@b.com");
        body.put("password","pw");

    when(userRepository.findByEmail("a@b.com")).thenReturn(java.util.Optional.empty());
    when(passwordEncoder.encode("pw")).thenReturn("hashed");
    when(jwtUtils.generateToken("a@b.com")).thenReturn("token");

    mockMvc.perform(post("/api/users/register").contentType("application/json").content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body)))
        .andExpect(status().isOk());
    }
}
