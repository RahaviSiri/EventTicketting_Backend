package com.SpringBoot.AdminService.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PaymentService", path = "/api/payments")
public interface PaymentClient {

   @GetMapping("/summary")
   Map<String, Object> getRevenueSummary(@RequestParam("range") String range);

   @GetMapping("/monthly")
  List<Map<String, Object>> getRevenueLast6Months();
}