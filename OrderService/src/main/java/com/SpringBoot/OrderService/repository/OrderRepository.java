package com.SpringBoot.OrderService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.SpringBoot.OrderService.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByEventId(Long eventId, Pageable pageable);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

}
