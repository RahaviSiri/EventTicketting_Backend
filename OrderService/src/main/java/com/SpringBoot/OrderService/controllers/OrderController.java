package com.SpringBoot.OrderService.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<Order>> getOrderByEventID(@PathVariable Long eventID) {
        List<Order> orders = orderService.getOrderByEventID(eventID);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<?> getOrderByUserID(@PathVariable Long userID) {
        List<Order> orders = orderService.getOrderByUserID(userID);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }
    
    

}
