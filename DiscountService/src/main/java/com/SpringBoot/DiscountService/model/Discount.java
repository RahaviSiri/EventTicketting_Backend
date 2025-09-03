package com.SpringBoot.DiscountService.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "discounts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private String code;  // e.g., "SUMMER50"

    private String description;

    private String discountType; // "PERCENTAGE" or "FLAT"

    private BigDecimal value;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private boolean isActive;

    private String imageURL;
}

