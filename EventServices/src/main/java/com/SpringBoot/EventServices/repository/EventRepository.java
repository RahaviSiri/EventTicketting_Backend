package com.SpringBoot.EventServices.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SpringBoot.EventServices.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {  
    List<Event> findByOrganizerId(Long organizerId); 
} 
