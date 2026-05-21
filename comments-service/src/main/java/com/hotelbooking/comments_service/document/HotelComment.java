package com.hotelbooking.comments_service.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hotel_comments")
public class HotelComment {

    @Id
    private String id;

    private Long hotelId;
    private String username;
    private String comment;
    private Integer rating;

    public HotelComment() {
    }

    public String getId() {
        return id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}