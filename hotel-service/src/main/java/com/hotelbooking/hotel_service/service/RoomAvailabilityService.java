package com.hotelbooking.hotel_service.service;

import com.hotelbooking.hotel_service.entity.RoomAvailability;
import com.hotelbooking.hotel_service.repository.RoomAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityService {

    private final RoomAvailabilityRepository availabilityRepository;

    public RoomAvailability createAvailability(RoomAvailability availability) {

        return availabilityRepository
                .findByHotelIdAndStartDateAndEndDate(
                        availability.getHotelId(),
                        availability.getStartDate(),
                        availability.getEndDate()
                )
                .map(existing -> {
                    existing.setAvailableRooms(availability.getAvailableRooms());
                    return availabilityRepository.save(existing);
                })
                .orElseGet(() -> availabilityRepository.save(availability));
    }

    public List<RoomAvailability> getAvailabilityByHotelId(Long hotelId) {
        return availabilityRepository.findByHotelId(hotelId);
    }
}