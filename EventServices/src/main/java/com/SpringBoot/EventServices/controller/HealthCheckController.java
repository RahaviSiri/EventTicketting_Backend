package com.SpringBoot.EventServices.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @RequestMapping(value = "/health", method = RequestMethod.HEAD)
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
