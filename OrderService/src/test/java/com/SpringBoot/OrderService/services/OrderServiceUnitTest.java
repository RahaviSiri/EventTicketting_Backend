package com.SpringBoot.OrderService.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.SpringBoot.OrderService.models.Order;
import com.SpringBoot.OrderService.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @BeforeEach
    void setup() {
    }

    @Test
    void getOrderByEventID_returnsPage() {
        Order o = Order.builder().id(1L).eventId(10L).userId(2L).attendeeEmail("a@b.com").attendeeName("A").price(100.0).status("Paid").build();
        Page<Order> page = new PageImpl<>(List.of(o));
        when(orderRepository.findByEventId(10L, PageRequest.of(0, 10))).thenReturn(page);

        Page<Order> result = orderService.getOrderByEventID(10L, PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEventId()).isEqualTo(10L);
    }

}
