package com.SpringBoot.DiscountService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SpringBoot.DiscountService.dto.DiscountDTO;
import com.SpringBoot.DiscountService.model.Discount;
import com.SpringBoot.DiscountService.repository.DiscountRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class DiscountService {

    @Autowired
    Cloudinary cloudinary;
    
    @Autowired
    DiscountRepository discountRepository;

    public Discount createDiscount(Long eventId, DiscountDTO discountDTO, MultipartFile imageFile) {
        String imageURL = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                imageURL = uploadResult.get("secure_url").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Determine if the discount is currently active
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = false;
        if (discountDTO.getValidFrom() != null && discountDTO.getValidTo() != null) {
            isActive = !now.isBefore(discountDTO.getValidFrom()) && !now.isAfter(discountDTO.getValidTo());
        }

        Discount discount = Discount.builder()
                .eventId(eventId)
                .code(discountDTO.getCode())
                .description(discountDTO.getDescription())
                .discountType(discountDTO.getDiscountType())
                .value(discountDTO.getValue())
                .validFrom(discountDTO.getValidFrom())
                .validTo(discountDTO.getValidTo())
                .isActive(isActive)
                .imageURL(imageURL)
                .build();

        return discountRepository.save(discount);
    }

    public List<Discount> getDiscountsByEventId(Long eventId) {
        System.out.println("Inside service layer, eventId: " + eventId);
        return discountRepository.findByEventId(eventId);
    }

    public Boolean validateDiscount(Long eventId, String code) {
        Discount discount = discountRepository.findByCodeAndEventIdAndIsActiveTrue(code, eventId);
        if (discount == null) {
            return false; 
        }
        LocalDateTime now = LocalDateTime.now();
        return !(now.isBefore(discount.getValidFrom()) || now.isAfter(discount.getValidTo()));
    }

    public void deleteById(Long discountId) {
        discountRepository.deleteById(discountId);
    }

    public void deactivateDiscount(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        discount.setActive(false);
        discountRepository.save(discount);
    }

    
}
