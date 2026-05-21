package com.hotelbooking.hotel_service.controller;

import com.hotelbooking.hotel_service.dto.HotelRequest;
import com.hotelbooking.hotel_service.dto.HotelResponse;
import com.hotelbooking.hotel_service.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public HotelResponse createHotel(
            @Valid @RequestBody HotelRequest request
    ) {

        return hotelService.createHotel(request);
    }

    @GetMapping
    public List<HotelResponse> getAllHotels() {

        return hotelService.getAllHotels();
    }
    
    @GetMapping("/page")
    public Page<HotelResponse> getAllHotelsPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {

    Pageable pageable = PageRequest.of(page, size);

    return hotelService.getAllHotelsPaginated(pageable);
    }

    @GetMapping("/{id}")
    public HotelResponse getHotelById(
            @PathVariable Long id
    ) {

        return hotelService.getHotelById(id);
    }

    @PutMapping("/{id}")
    public HotelResponse updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request
    ) {

        return hotelService.updateHotel(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteHotel(
            @PathVariable Long id
    ) {

        hotelService.deleteHotel(id);

        return "Hotel deleted successfully!";
    }

    @GetMapping("/search")
    public List<HotelResponse> searchHotelsByCity(
            @RequestParam String city
    ) {

        return hotelService.searchHotelsByCity(city);
    }

    @GetMapping("/search/available")
    public List<HotelResponse> searchAvailableHotels(
            @RequestParam String city,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer people
    ) {

        return hotelService.searchAvailableHotels(
                city,
                startDate,
                endDate,
                people
        );
    }

    @GetMapping("/search/available/discounted")
    public List<HotelResponse> searchAvailableHotelsWithDiscount(
            @RequestParam String city,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer people
    ) {

        return hotelService.searchAvailableHotelsWithDiscount(
                city,
                startDate,
                endDate,
                people
        );
    }
}