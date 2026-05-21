package com.hotelbooking.ai_agent_service.dto;

public class HotelResponse {

    private Long id;
    private String name;
    private String city;
    private String address;
    private Double rating;
    private Double pricePerNight;
    private Boolean hasPool;
    private Boolean hasWifi;
    private Boolean hasBreakfast;

    public HotelResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public Double getRating() {
        return rating;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public Boolean getHasPool() {
        return hasPool;
    }

    public Boolean getHasWifi() {
        return hasWifi;
    }

    public Boolean getHasBreakfast() {
        return hasBreakfast;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setHasPool(Boolean hasPool) {
        this.hasPool = hasPool;
    }

    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public void setHasBreakfast(Boolean hasBreakfast) {
        this.hasBreakfast = hasBreakfast;
    }
}