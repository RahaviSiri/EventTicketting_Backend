package com.event.PaymentService.util;

import java.time.LocalDateTime;

public class DateRangeUtil {

    public static LocalDateTime resolveFrom(String range) {
        LocalDateTime now = LocalDateTime.now();

        return switch (range.toLowerCase()) {
            case "last7days" -> now.minusDays(7);
            case "thismonth" -> now.withDayOfMonth(1);
            case "last3months" -> now.minusMonths(3).withDayOfMonth(1);
            case "thisyear" -> now.withDayOfYear(1);
            default -> now.minusDays(7); // fallback
        };
    }
}
