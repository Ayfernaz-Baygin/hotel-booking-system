package com.hotelbooking.hotel_service.controller;

import com.hotelbooking.hotel_service.entity.ReservationQueueItem;
import com.hotelbooking.hotel_service.repository.ReservationQueueRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation-queue")
public class ReservationQueueController {

    private final ReservationQueueRepository reservationQueueRepository;

    public ReservationQueueController(ReservationQueueRepository reservationQueueRepository) {
        this.reservationQueueRepository = reservationQueueRepository;
    }

    @GetMapping("/unprocessed")
    public List<ReservationQueueItem> getUnprocessedReservations() {
        return reservationQueueRepository.findByProcessedFalse();
    }

    @PutMapping("/{id}/processed")
    public String markAsProcessed(@PathVariable Long id) {

        ReservationQueueItem item =
                reservationQueueRepository.findById(id).orElseThrow();

        item.setProcessed(true);

        reservationQueueRepository.save(item);

        return "Queue item marked as processed.";
    }
}