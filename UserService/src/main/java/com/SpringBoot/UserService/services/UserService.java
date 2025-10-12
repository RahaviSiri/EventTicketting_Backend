package com.SpringBoot.UserService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.SpringBoot.UserService.dto.UserDTO;
import com.SpringBoot.UserService.model.User;
import com.SpringBoot.UserService.repository.UserRepository;
import com.SpringBoot.UserService.utils.DateRangeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserNotification UserNotification;

    public void registerUser(User user) {
        // Save user
        userRepository.save(user);

        // Publish welcome email
        try {
            String payload = String.format(
                    "{ \"email\": \"%s\", \"name\": \"%s\" }",
                    user.getEmail(),
                    user.getName());

            UserNotification.publishUserCreated(payload);
            System.out.println("Published welcome email for userId=" + user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserRole(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getRole();
        }
        return "No user Found";
    }

    public void setUserRole(String email, String role) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("User found, updating role to " + role);
            user.get().setRole(role);
            userRepository.save(user.get());
        } else {
            System.out.println("User not found for email: " + email);
        }
    }

    public Long getUserID(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println(user.get().getId());
            return user.get().getId();
        }
        return null;
    }
    
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .orElse(null);
    }

    public void changePassword(String email, String hashedPassword) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPasswordHash(hashedPassword);
            userRepository.save(user.get());
        } else {
            System.out.println("User not found for email: " + email);
        }
    }

    //        admin part  
    
    
    public Map<String, Object> getOrganizersSummary(String range) {
        LocalDateTime fromDate = DateRangeUtil.resolveFrom(range);
        LocalDateTime toDate = LocalDateTime.now();
        
        // Current period organizers
        int total = userRepository.countOrganizersBetween(fromDate, toDate);
        
        // Previous period organizers (same length)
        long days = java.time.Duration.between(fromDate, toDate).toDays();
        LocalDateTime previousFrom = fromDate.minusDays(days);
        LocalDateTime previousTo = fromDate;
        int previous = userRepository.countOrganizersBetween(previousFrom, previousTo);
        
        double trend = previous == 0 ? 0 : ((double) (total - previous) / previous) * 100;
        
        return Map.of(
            "total", total,
            "trend", trend);
        }
        
        public List<Map<String, Object>> getSignupsLast6Months() {
            List<Map<String, Object>> result = new ArrayList<>();
            
            LocalDate now = LocalDate.now();
            for (int i = 5; i >= 0; i--) {
                YearMonth month = YearMonth.from(now.minusMonths(i));
                int signups = userRepository.countSignupsByMonth(month.getYear(), month.getMonthValue());
                result.add(Map.of(
                    "month", month.getMonth().name().substring(0, 3),
                    "signups", signups
                    ));
                }
                return result;
            }
           
            public Page<UserDTO> getAllOrganizers(Pageable pageable) {
                Page<User> organizers = userRepository.findByRole("ORGANIZER", pageable);

                return organizers.map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()));
            }

}
