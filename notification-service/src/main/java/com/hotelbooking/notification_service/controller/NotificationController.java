package com.hotelbooking.notification_service.controller;

import com.hotelbooking.notification_service.dto.LowCapacityNotificationResponse;
import com.hotelbooking.notification_service.dto.ProcessedReservationResponse;
import com.hotelbooking.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/low-capacity")
    public List<LowCapacityNotificationResponse> getLowCapacityNotifications() {
        return notificationService.checkLowCapacityHotels();
    }

    @GetMapping("/process-reservations")
    public List<ProcessedReservationResponse> processReservationQueue() {
        return notificationService.processReservationQueue();
    }
}