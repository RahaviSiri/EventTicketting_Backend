package com.SpringBoot.DiscountService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SpringBoot.DiscountService.model.Discount;
import java.util.List;


@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByEventId(Long eventId);
    Discount findByCodeAndEventIdAndIsActiveTrue(String code, Long eventId);
}
