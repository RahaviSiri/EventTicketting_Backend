package com.SpringBoot.OrderService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.SpringBoot.OrderService")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.SpringBoot.OrderService.services")
public class OrderServiceApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Starting OrderService Application...");
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("✅ Feign clients should now be registered if scanning worked!");
    }
}
