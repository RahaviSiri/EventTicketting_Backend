package com.SpringBoot.OrderService.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.SpringBoot.OrderService.models.Order;
import com.SpringBoot.OrderService.services.OrderService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void getOrdersByEventID_returnsOk() throws Exception {
        when(orderService.getOrderByEventID(1L, PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/orders/event/1"))
                .andExpect(status().isOk());
    }
}
