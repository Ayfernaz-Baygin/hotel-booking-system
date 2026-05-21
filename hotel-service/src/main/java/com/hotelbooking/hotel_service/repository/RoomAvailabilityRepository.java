package com.hotelbooking.hotel_service.repository;

import com.hotelbooking.hotel_service.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findByHotelId(Long hotelId);

    Optional<RoomAvailability> findByHotelIdAndStartDateAndEndDate(
            Long hotelId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<RoomAvailability> findByHotelIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long hotelId,
            LocalDate requestedEndDate,
            LocalDate requestedStartDate
    );
}