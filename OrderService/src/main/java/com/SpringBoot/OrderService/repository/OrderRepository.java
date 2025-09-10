package com.SpringBoot.OrderService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringBoot.OrderService.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEventId(Long eventId);
    List<Order> findByUserId(Long userId);
}
