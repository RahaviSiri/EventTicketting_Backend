package com.SpringBoot.NotificationService.controllers;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.SpringBoot.NotificationService.services.ContactKafkaProducer;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ContactController.class)
class ContactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ContactKafkaProducer producer;

    @Test
    void contact_sendsMessage() throws Exception {
        doNothing().when(producer).sendMessage(org.mockito.ArgumentMatchers.anyString());

        var body = Map.of("name", "A", "message", "hello");
        mockMvc.perform(post("/api/notifications/contact").contentType("application/json")
                .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body)))
                .andExpect(status().isOk());
    }
}
