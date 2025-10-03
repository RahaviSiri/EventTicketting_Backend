package com.SpringBoot.OrderService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SpringBoot.OrderService.dto.OrderCreateDTO;
import com.SpringBoot.OrderService.models.Order;
import com.SpringBoot.OrderService.services.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/event/{eventID}")
    public ResponseEntity<Page<Order>> getOrdersByEventID(
            @PathVariable Long eventID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getOrderByEventID(eventID, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<Page<Order>> getOrdersByUserID(
            @PathVariable Long userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Integer month, // 1-12
            @RequestParam(required = false) Integer year) {
                
        // Even though your JPQL query looks like it selects all bookings for a month, pagination is applied at the database level, so only the requested page is actually returned.

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if (month != null && year != null) {
            orders = orderService.getOrdersByUserIDAndMonth(userID, month, year, pageable);
        } else {
            orders = orderService.getOrderByUserID(userID, pageable);
        }

        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

}
