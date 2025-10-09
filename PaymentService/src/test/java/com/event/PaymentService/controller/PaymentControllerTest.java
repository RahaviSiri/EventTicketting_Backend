package com.event.PaymentService.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.event.PaymentService.service.PaymentService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void getPaymentById_returnsNotFoundForMissing() throws Exception {
        when(paymentService.getPaymentById(999L)).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());
    }
}
