package com.SpringBoot.DiscountService.controller;

import java.util.List;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.DiscountService.dto.DiscountDTO;
import com.SpringBoot.DiscountService.model.Discount;
import com.SpringBoot.DiscountService.service.DiscountService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    DiscountService discountService;

    @PostMapping(value = "/event/{eventId}/file", consumes = "multipart/form-data")
    public ResponseEntity<Discount> createDiscountWithFile(@PathVariable Long eventId,      @RequestPart("discount") @Validated DiscountDTO discountDTO, @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Discount savedDiscount = discountService.createDiscount(eventId, discountDTO, imageFile);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(savedDiscount);
    }


    @GetMapping("/event/{eventId}")
    public List<Discount> getDiscountByEventId(@PathVariable Long eventId) {
        System.out.println("Fetching discounts for event ID: " + eventId);
        List<Discount> discounts = discountService.getDiscountsByEventId(eventId);
        return discounts;
    }

    @GetMapping("/validate/{eventId}/{code}")
    public ResponseEntity<Boolean> validateDiscount(@PathVariable Long eventId, @PathVariable String code) {
        Boolean isValid = discountService.validateDiscount(eventId, code);
        return ResponseEntity.ok(isValid);
    }

    @DeleteMapping("/{id}")
    public void deleteDiscount(@PathVariable Long id) {
        discountService.deleteById(id);
    }

    @PutMapping("/{discountId}/deactivate")
    public ResponseEntity<Void> deactivateDiscount(@PathVariable Long discountId) {
        discountService.deactivateDiscount(discountId);
        return ResponseEntity.ok().build();
    }

    

}
