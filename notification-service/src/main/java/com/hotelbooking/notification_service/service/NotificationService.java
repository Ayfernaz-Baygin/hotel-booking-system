package com.hotelbooking.notification_service.service;

import com.hotelbooking.notification_service.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final WebClient hotelServiceClient;

    public NotificationService(WebClient hotelServiceClient) {
        this.hotelServiceClient = hotelServiceClient;
    }

    public List<LowCapacityNotificationResponse> checkLowCapacityHotels() {

        List<HotelDto> hotels = hotelServiceClient.get()
                .uri("/api/v1/hotels")
                .retrieve()
                .bodyToFlux(HotelDto.class)
                .collectList()
                .block();

        if (hotels == null) {
            return List.of();
        }

        return hotels.stream()
                .filter(hotel -> hotel.getAvailableRooms() < hotel.getTotalRooms() * 0.2)
                .map(hotel -> new LowCapacityNotificationResponse(
                        hotel.getId(),
                        hotel.getName(),
                        hotel.getAvailableRooms(),
                        hotel.getTotalRooms(),
                        (double) hotel.getAvailableRooms() / hotel.getTotalRooms()
                ))
                .collect(Collectors.toList());
    }

    public List<ProcessedReservationResponse> processReservationQueue() {

        List<ReservationQueueItemDto> items = hotelServiceClient.get()
                .uri("/api/v1/reservation-queue/unprocessed")
                .retrieve()
                .bodyToFlux(ReservationQueueItemDto.class)
                .collectList()
                .block();

        if (items == null || items.isEmpty()) {
            return List.of();
        }

        return items.stream()
                .map(this::processSingleReservation)
                .collect(Collectors.toList());
    }

    private ProcessedReservationResponse processSingleReservation(ReservationQueueItemDto item) {

        hotelServiceClient.put()
                .uri("/api/v1/reservation-queue/" + item.getId() + "/processed")
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        System.out.println("ADMIN NOTIFICATION → New reservation: " + item.getMessage());

        return new ProcessedReservationResponse(item.getId(), "Processed");
    }
}
