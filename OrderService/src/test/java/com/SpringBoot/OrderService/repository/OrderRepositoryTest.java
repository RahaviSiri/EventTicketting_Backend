package com.SpringBoot.OrderService.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.SpringBoot.OrderService.models.Order;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void saveAndFindByEventId() {
        Order o = Order.builder().eventId(5L).userId(2L).attendeeEmail("x@y.com").attendeeName("X").price(50.0).status("Paid").build();
        Order saved = orderRepository.save(o);

        assertThat(saved.getId()).isNotNull();

        var page = orderRepository.findByEventId(5L, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
    }
}
