package com.hotelbooking.hotel_service.repository;

import com.hotelbooking.hotel_service.entity.ReservationQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationQueueRepository extends JpaRepository<ReservationQueueItem, Long> {

    List<ReservationQueueItem> findByProcessedFalse();
}