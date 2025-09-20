package com.SpringBoot.OrderService.services;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringBoot.OrderService.dto.OrderCreateDTO;
import com.SpringBoot.OrderService.dto.UserDTO;
import com.SpringBoot.OrderService.models.Order;
import com.SpringBoot.OrderService.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserServiceClient userServiceClient;

    public Page<Order> getOrderByEventID(Long eventID, Pageable pageable) {
        return orderRepository.findByEventId(eventID, pageable);
    }

    public Page<Order> getOrderByUserID(Long userID, Pageable pageable) {
        return orderRepository.findByUserId(userID, pageable);
    }

    public Object createOrder(OrderCreateDTO dto) {
        // 1. Get user details from User Service
        UserDTO user = userServiceClient.getUserById(dto.getUserId());
        System.out.println(user.getName());

        // 2. Build Order
        Order order = Order.builder()
                .eventId(dto.getEventId())
                .userId(dto.getUserId())
                .ticketId(dto.getTicketId())
                .price(dto.getPrice())
                .attendeeName(user.getName())
                .attendeeEmail(user.getEmail())
                .status("Paid")
                .checkIn(false)
                .createdAt(LocalDateTime.now())
                .build();

        // 3. Save order
        return orderRepository.save(order);
    }
    
}
