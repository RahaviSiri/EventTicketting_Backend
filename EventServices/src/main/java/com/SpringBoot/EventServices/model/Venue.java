package com.SpringBoot.EventServices.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Integer capacity;
    private String description;
}
