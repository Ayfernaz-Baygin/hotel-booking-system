package com.hotelbooking.hotel_service.service;

import com.hotelbooking.hotel_service.dto.HotelRequest;
import com.hotelbooking.hotel_service.dto.HotelResponse;
import com.hotelbooking.hotel_service.entity.Hotel;
import com.hotelbooking.hotel_service.entity.RoomAvailability;
import com.hotelbooking.hotel_service.exception.ResourceNotFoundException;
import com.hotelbooking.hotel_service.mapper.HotelMapper;
import com.hotelbooking.hotel_service.repository.HotelRepository;
import com.hotelbooking.hotel_service.repository.RoomAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    @CacheEvict(value = {"hotelDetails", "hotelSearch"}, allEntries = true)
    public HotelResponse createHotel(HotelRequest request) {
        Hotel hotel = HotelMapper.toEntity(request);
        Hotel savedHotel = hotelRepository.save(hotel);
        return HotelMapper.toResponse(savedHotel);
    }

    @Cacheable(value = "hotelSearch", key = "'allHotels'")
    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(HotelMapper::toResponse)
                .toList();
    }

    public Page<HotelResponse> getAllHotelsPaginated(Pageable pageable) {
        return hotelRepository.findAll(pageable)
                .map(HotelMapper::toResponse);
    }

    @Cacheable(value = "hotelDetails", key = "#id")
    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with id: " + id)
                );

        return HotelMapper.toResponse(hotel);
    }

    @CacheEvict(value = {"hotelDetails", "hotelSearch"}, allEntries = true)
    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with id: " + id)
                );

        existingHotel.setName(request.getName());
        existingHotel.setCity(request.getCity());
        existingHotel.setAddress(request.getAddress());
        existingHotel.setDescription(request.getDescription());
        existingHotel.setRating(request.getRating());
        existingHotel.setTotalRooms(request.getTotalRooms());
        existingHotel.setAvailableRooms(request.getAvailableRooms());
        existingHotel.setPricePerNight(request.getPricePerNight());
        existingHotel.setHasPool(request.getHasPool());
        existingHotel.setHasWifi(request.getHasWifi());
        existingHotel.setHasBreakfast(request.getHasBreakfast());

        Hotel updatedHotel = hotelRepository.save(existingHotel);

        return HotelMapper.toResponse(updatedHotel);
    }

    @CacheEvict(value = {"hotelDetails", "hotelSearch"}, allEntries = true)
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with id: " + id)
                );

        hotelRepository.delete(hotel);
    }

    @Cacheable(value = "hotelSearch", key = "'city:' + #city.toLowerCase()")
    public List<HotelResponse> searchHotelsByCity(String city) {
        return hotelRepository.findByCityIgnoreCase(city)
                .stream()
                .map(HotelMapper::toResponse)
                .toList();
    }

    @Cacheable(
            value = "hotelSearch",
            key = "'available:' + #city.toLowerCase() + ':' + #startDate + ':' + #endDate + ':' + #people"
    )
    public List<HotelResponse> searchAvailableHotels(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Integer people
    ) {
        List<Hotel> hotels = hotelRepository.findByCityIgnoreCase(city);

        return hotels.stream()
                .map(hotel -> {
                    List<RoomAvailability> availabilityList =
                            roomAvailabilityRepository
                                    .findByHotelIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                            hotel.getId(),
                                            endDate,
                                            startDate
                                    );

                    RoomAvailability bestAvailability = availabilityList.stream()
                            .filter(availability ->
                                    availability.getAvailableRooms() >= people
                            )
                            .sorted((a1, a2) -> {
                                int roomCompare = Integer.compare(
                                        a2.getAvailableRooms(),
                                        a1.getAvailableRooms()
                                );

                                if (roomCompare != 0) {
                                    return roomCompare;
                                }

                                return Long.compare(
                                        a2.getId(),
                                        a1.getId()
                                );
                            })
                            .findFirst()
                            .orElse(null);

                    if (bestAvailability == null) {
                        return null;
                    }

                    HotelResponse response = HotelMapper.toResponse(hotel);
                    response.setAvailableRooms(bestAvailability.getAvailableRooms());

                    return response;
                })
                .filter(response -> response != null)
                .toList();
    }

    public List<HotelResponse> searchAvailableHotelsWithDiscount(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Integer people
    ) {
        return searchAvailableHotels(city, startDate, endDate, people)
                .stream()
                .peek(hotel ->
                        hotel.setPricePerNight(
                                hotel.getPricePerNight() * 0.85
                        )
                )
                .toList();
    }
}