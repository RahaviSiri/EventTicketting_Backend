package com.event.PaymentService.mapper;

import com.event.PaymentService.dto.PaymentDTO;
import com.event.PaymentService.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    PaymentDTO toDto(Payment payment);
    
}


