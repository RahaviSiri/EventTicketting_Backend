package com.SpringBoot.DiscountService.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DiscountDTO {
    private String code;
    private String description;
    private String discountType;
    private BigDecimal value;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}

