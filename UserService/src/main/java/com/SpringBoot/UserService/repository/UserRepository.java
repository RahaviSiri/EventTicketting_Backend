package com.SpringBoot.UserService.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringBoot.UserService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // for admin part


    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ORGANIZER' AND u.createdAt >= :from AND u.createdAt < :to")
    int countOrganizersBetween(@Param("from") LocalDateTime from,
                  @Param("to") LocalDateTime to);
    
    //monthly signups
    @Query("SELECT COUNT(u) " +
           "FROM User u " +
           "WHERE u.role IN ('ORGANIZER', 'ATTENDEE') " +
           "AND YEAR(u.createdAt) = :year " +
           "AND MONTH(u.createdAt) = :month")
    int countSignupsByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(u) " +
           "FROM User u " +
           "WHERE u.role = 'ORGANIZER' " +
           "AND YEAR(u.createdAt) = :year " +
           "AND MONTH(u.createdAt) = :month")
    int countOrganizersByMonth(@Param("year") int year, @Param("month") int month);

    Page<User> findByRole(String role, Pageable pageable);
}
