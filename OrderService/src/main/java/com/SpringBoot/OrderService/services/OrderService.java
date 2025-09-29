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


    @Autowired
    NotificationProducer notificationProducer;

    public List<Order> getOrderByEventID(Long eventID) {
        return orderRepository.findByEventId(eventID);

    public Page<Order> getOrderByEventID(Long eventID, Pageable pageable) {
        return orderRepository.findByEventId(eventID, pageable);

    }

    public Page<Order> getOrderByUserID(Long userID, Pageable pageable) {
        return orderRepository.findByUserId(userID, pageable);
    }

    public Object createOrder(OrderCreateDTO dto) {
        // 1. Get user details
    UserDTO user = userServiceClient.getUserById(dto.getUserId());
    System.out.println("Fetched user details: " + user);

    // 2. Build order
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
    System.out.println("Creating order: ");
    Order savedOrder = orderRepository.save(order);

    // 3. Publish event to Kafka
    System.out.println("Order created: ");
    String eventMessage = String.format(
        "{ \"orderId\": %d, \"userEmail\": \"%s\", \"eventId\": %d, \"ticketId\": %d, \"price\": %.2f }",
        savedOrder.getId(), savedOrder.getAttendeeEmail(), savedOrder.getEventId(),
        savedOrder.getTicketId(), savedOrder.getPrice(), savedOrder.getCreatedAt()
    );
    notificationProducer.sendOrderConfirmed(eventMessage);
    System.out.println("Published order confirmation to Kafka: " + eventMessage);

    return savedOrder;
    }
    
}
