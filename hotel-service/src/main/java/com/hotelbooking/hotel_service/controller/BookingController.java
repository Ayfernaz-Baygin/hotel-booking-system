package com.hotelbooking.hotel_service.controller;

import com.hotelbooking.hotel_service.entity.Booking;
import com.hotelbooking.hotel_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
      public Booking createBooking(@Valid @RequestBody Booking booking) {
    return bookingService.createBooking(booking);
    }

    @GetMapping
public List<Booking> getAllBookings() {
    return bookingService.getAllBookings();
   }

   @GetMapping("/{id}")
   public Booking getBookingById(@PathVariable Long id) {
    return bookingService.getBookingById(id);
    }

    @DeleteMapping("/{id}")
public String cancelBooking(@PathVariable Long id) {

    bookingService.cancelBooking(id);

     return "Booking cancelled successfully!";
   }
}