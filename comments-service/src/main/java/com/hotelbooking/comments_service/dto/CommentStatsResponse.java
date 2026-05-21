package com.hotelbooking.comments_service.dto;

import java.util.Map;

public class CommentStatsResponse {

    private Long hotelId;
    private Double averageRating;
    private Integer totalComments;
    private Map<Integer, Long> ratingDistribution;

    public CommentStatsResponse() {
    }

    public CommentStatsResponse(Long hotelId, Double averageRating, Integer totalComments, Map<Integer, Long> ratingDistribution) {
        this.hotelId = hotelId;
        this.averageRating = averageRating;
        this.totalComments = totalComments;
        this.ratingDistribution = ratingDistribution;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public Map<Integer, Long> getRatingDistribution() {
        return ratingDistribution;
    }
}