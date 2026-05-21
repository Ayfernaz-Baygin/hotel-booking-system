package com.hotelbooking.hotel_service.config;

import com.hotelbooking.hotel_service.entity.Hotel;
import com.hotelbooking.hotel_service.entity.RoomAvailability;
import com.hotelbooking.hotel_service.repository.HotelRepository;
import com.hotelbooking.hotel_service.repository.RoomAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    @Override
    public void run(String... args) {

        if (hotelRepository.count() > 0) {
            return;
        }

        hotelRepository.save(Hotel.builder()
                .name("Hotel Swiss Updated")
                .city("Bodrum")
                .address("Bodrum Marina Updated")
                .description("Updated luxury seaside hotel")
                .availableRooms(37)
                .totalRooms(130)
                .rating(4.9)
                .pricePerNight(9200.0)
                .hasBreakfast(true)
                .hasPool(true)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Bodrum Resort")
                .city("Bodrum")
                .address("Yalikavak Marina")
                .description("Luxury resort hotel with private beach and spa")
                .availableRooms(45)
                .totalRooms(120)
                .rating(4.7)
                .pricePerNight(8500.0)
                .hasBreakfast(true)
                .hasPool(true)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Sea Pearl Hotel")
                .city("Bodrum")
                .address("Gumbet Beach")
                .description("Modern seaside hotel with sunset view")
                .availableRooms(45)
                .totalRooms(120)
                .rating(4.7)
                .pricePerNight(8500.0)
                .hasBreakfast(true)
                .hasPool(true)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Izmir Marina Hotel")
                .city("izmir")
                .address("Kordon")
                .description("Luxury hotel in Izmir")
                .availableRooms(78)
                .totalRooms(120)
                .rating(4.8)
                .pricePerNight(7500.0)
                .hasBreakfast(true)
                .hasPool(true)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Antalya Beach Resort")
                .city("antalya")
                .address("Lara Beach")
                .description("Luxury beach resort with pool and breakfast")
                .availableRooms(90)
                .totalRooms(150)
                .rating(4.9)
                .pricePerNight(9800.0)
                .hasBreakfast(true)
                .hasPool(true)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Antalya Beach Resort")
                .city("antalya")
                .address("Lara Beach")
                .description("Luxury beach resort with pool and breakfast")
                .availableRooms(90)
                .totalRooms(150)
                .rating(4.9)
                .pricePerNight(9800.0)
                .hasBreakfast(true)
                .hasPool(true)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Istanbul City Hotel")
                .city("istanbul")
                .address("Taksim")
                .description("Modern city hotel close to shopping and transport")
                .availableRooms(55)
                .totalRooms(110)
                .rating(4.6)
                .pricePerNight(7200.0)
                .hasBreakfast(true)
                .hasPool(false)
                .hasWifi(true)
                .build());

        hotelRepository.save(Hotel.builder()
                .name("Ankara Business Hotel")
                .city("ankara")
                .address("Kizilay")
                .description("Comfortable business hotel in the city center")
                .availableRooms(24)
                .totalRooms(80)
                .rating(4.4)
                .pricePerNight(5200.0)
                .hasBreakfast(true)
                .hasPool(false)
                .hasWifi(true)
                .build());

        LocalDate today = LocalDate.now();

        for (Hotel hotel : hotelRepository.findAll()) {
            roomAvailabilityRepository.save(
                    RoomAvailability.builder()
                            .hotelId(hotel.getId())
                            .startDate(today)
                            .endDate(today.plusMonths(6))
                            .availableRooms(hotel.getAvailableRooms())
                            .build()
            );
        }

        System.out.println("Sample hotels and room availability inserted into H2 database.");
    }
}