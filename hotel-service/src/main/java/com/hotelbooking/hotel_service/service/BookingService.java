package com.hotelbooking.hotel_service.service;

import com.hotelbooking.hotel_service.entity.Booking;
import com.hotelbooking.hotel_service.entity.Hotel;
import com.hotelbooking.hotel_service.entity.ReservationQueueItem;
import com.hotelbooking.hotel_service.entity.RoomAvailability;
import com.hotelbooking.hotel_service.exception.BadRequestException;
import com.hotelbooking.hotel_service.exception.ResourceNotFoundException;
import com.hotelbooking.hotel_service.repository.BookingRepository;
import com.hotelbooking.hotel_service.repository.HotelRepository;
import com.hotelbooking.hotel_service.repository.ReservationQueueRepository;
import com.hotelbooking.hotel_service.repository.RoomAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final ReservationQueueRepository reservationQueueRepository;

    @Transactional
    public Booking createBooking(Booking booking) {

        long nights = ChronoUnit.DAYS.between(
                booking.getStartDate(),
                booking.getEndDate()
        );

        if (nights <= 0) {
            throw new BadRequestException("End date must be after start date.");
        }

        Hotel hotel = hotelRepository.findById(booking.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + booking.getHotelId()
                ));

        List<RoomAvailability> availabilityList =
                roomAvailabilityRepository
                        .findByHotelIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                booking.getHotelId(),
                                booking.getEndDate(),
                                booking.getStartDate()
                        );

        RoomAvailability selectedAvailability = availabilityList.stream()
        .filter(availability ->
                availability.getAvailableRooms() >= booking.getPeopleCount()
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
        .orElseThrow(() ->
                new BadRequestException("Not enough available rooms for selected dates.")
        );

        if (hotel.getAvailableRooms() < booking.getPeopleCount()) {
            throw new BadRequestException("Not enough general hotel capacity.");
        }

        selectedAvailability.setAvailableRooms(
                selectedAvailability.getAvailableRooms() - booking.getPeopleCount()
        );

        roomAvailabilityRepository.save(selectedAvailability);

        hotel.setAvailableRooms(hotel.getAvailableRooms() - booking.getPeopleCount());
        hotelRepository.save(hotel);

        double discountedPricePerNight = hotel.getPricePerNight() * 0.85;
        double totalPrice = nights * discountedPricePerNight * booking.getPeopleCount();

        booking.setTotalPrice(totalPrice);
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);

        ReservationQueueItem queueItem = ReservationQueueItem.builder()
                .bookingId(savedBooking.getId())
                .hotelId(savedBooking.getHotelId())
                .guestName(savedBooking.getGuestName())
                .message(
                        "New reservation created. Booking ID: " + savedBooking.getId()
                                + ", Hotel ID: " + savedBooking.getHotelId()
                                + ", Guest: " + savedBooking.getGuestName()
                                + ", Dates: " + savedBooking.getStartDate()
                                + " - " + savedBooking.getEndDate()
                                + ", People: " + savedBooking.getPeopleCount()
                                + ", Total Price: " + savedBooking.getTotalPrice()
                )
                .processed(false)
                .createdAt(LocalDateTime.now())
                .build();

        reservationQueueRepository.save(queueItem);

        return savedBooking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + id
                ));
    }

    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId
                ));

        Hotel hotel = hotelRepository.findById(booking.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + booking.getHotelId()
                ));

        List<RoomAvailability> availabilityList =
                roomAvailabilityRepository
                        .findByHotelIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                booking.getHotelId(),
                                booking.getEndDate(),
                                booking.getStartDate()
                        );

        if (!availabilityList.isEmpty()) {
            RoomAvailability selectedAvailability = availabilityList.stream()
                    .max(Comparator.comparing(RoomAvailability::getAvailableRooms))
                    .orElse(availabilityList.get(0));

            selectedAvailability.setAvailableRooms(
                    selectedAvailability.getAvailableRooms() + booking.getPeopleCount()
            );

            roomAvailabilityRepository.save(selectedAvailability);
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms() + booking.getPeopleCount());
        hotelRepository.save(hotel);

        bookingRepository.deleteById(bookingId);
    }
}