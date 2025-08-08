package com.SpringBoot.EventServices.dto;

import lombok.Data;

@Data
public class VenueDTO {

    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Integer capacity;
    private String description;
}
