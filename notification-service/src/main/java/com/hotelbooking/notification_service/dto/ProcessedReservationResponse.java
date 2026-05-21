package com.hotelbooking.notification_service.dto;

public class ProcessedReservationResponse {

    private Long queueItemId;
    private String status;

    public ProcessedReservationResponse() {
    }

    public ProcessedReservationResponse(Long queueItemId, String status) {
        this.queueItemId = queueItemId;
        this.status = status;
    }

    public Long getQueueItemId() {
        return queueItemId;
    }

    public void setQueueItemId(Long queueItemId) {
        this.queueItemId = queueItemId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
