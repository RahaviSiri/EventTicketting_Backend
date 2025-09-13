package com.SpringBoot.OrderService.services;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<Order> getOrderByEventID(Long eventID) {
        return orderRepository.findByEventId(eventID);
    }

    public List<Order> getOrderByUserID(Long userID) {
        return orderRepository.findByUserId(userID);
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
