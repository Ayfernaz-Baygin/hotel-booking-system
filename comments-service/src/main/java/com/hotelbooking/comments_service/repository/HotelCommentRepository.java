package com.hotelbooking.comments_service.repository;

import com.hotelbooking.comments_service.document.HotelComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HotelCommentRepository extends MongoRepository<HotelComment, String> {

    List<HotelComment> findByHotelId(Long hotelId);
}