package com.hotelbooking.notification_service.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationSchedulerService {

    private final NotificationService notificationService;

    public NotificationSchedulerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void nightlyLowCapacityCheck() {
        List<?> results = notificationService.checkLowCapacityHotels();
        System.out.println("Nightly capacity check completed.");
        results.forEach(System.out::println);
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void reservationQueueProcessor() {
        List<?> results = notificationService.processReservationQueue();
        results.forEach(System.out::println);
    }
}
