package com.hotelbooking.hotel_service.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse implements Serializable {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private Double rating;
    private Integer totalRooms;
    private Integer availableRooms;
    private Double pricePerNight;
    private Boolean hasPool;
    private Boolean hasWifi;
    private Boolean hasBreakfast;
}