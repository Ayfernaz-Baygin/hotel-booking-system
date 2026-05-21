package com.hotelbooking.hotel_service.controller;

import com.hotelbooking.hotel_service.entity.RoomAvailability;
import com.hotelbooking.hotel_service.service.RoomAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class RoomAvailabilityController {

    private final RoomAvailabilityService availabilityService;

    @PostMapping
    public RoomAvailability createAvailability(@Valid @RequestBody RoomAvailability availability) {
        return availabilityService.createAvailability(availability);
    }

    @GetMapping("/hotel/{hotelId}")
    public List<RoomAvailability> getAvailabilityByHotelId(@PathVariable Long hotelId) {
        return availabilityService.getAvailabilityByHotelId(hotelId);
    }
}