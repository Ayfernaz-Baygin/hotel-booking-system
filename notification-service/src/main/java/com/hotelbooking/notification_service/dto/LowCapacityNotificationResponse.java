package com.hotelbooking.notification_service.dto;

public class LowCapacityNotificationResponse {

    private Long hotelId;
    private String hotelName;
    private Integer availableRooms;
    private Integer totalRooms;
    private double percentage;

    public LowCapacityNotificationResponse() {
    }

    public LowCapacityNotificationResponse(
            Long hotelId,
            String hotelName,
            Integer availableRooms,
            Integer totalRooms,
            double percentage
    ) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.availableRooms = availableRooms;
        this.totalRooms = totalRooms;
        this.percentage = percentage;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
