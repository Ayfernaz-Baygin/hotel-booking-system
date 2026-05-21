package com.hotelbooking.hotel_service.mapper;

import com.hotelbooking.hotel_service.dto.HotelRequest;
import com.hotelbooking.hotel_service.dto.HotelResponse;
import com.hotelbooking.hotel_service.entity.Hotel;

public class HotelMapper {

    public static Hotel toEntity(HotelRequest request) {
        return Hotel.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .description(request.getDescription())
                .rating(request.getRating())
                .totalRooms(request.getTotalRooms())
                .availableRooms(request.getAvailableRooms())
                .pricePerNight(request.getPricePerNight())
                .hasPool(request.getHasPool())
                .hasWifi(request.getHasWifi())
                .hasBreakfast(request.getHasBreakfast())
                .build();
    }

    public static HotelResponse toResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .totalRooms(hotel.getTotalRooms())
                .availableRooms(hotel.getAvailableRooms())
                .pricePerNight(hotel.getPricePerNight())
                .hasPool(hotel.getHasPool())
                .hasWifi(hotel.getHasWifi())
                .hasBreakfast(hotel.getHasBreakfast())
                .build();
    }
}