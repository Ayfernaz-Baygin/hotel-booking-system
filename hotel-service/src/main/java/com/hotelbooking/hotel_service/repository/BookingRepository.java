package com.hotelbooking.hotel_service.repository;

import com.hotelbooking.hotel_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}