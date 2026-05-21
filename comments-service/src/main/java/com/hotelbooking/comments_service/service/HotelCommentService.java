package com.hotelbooking.comments_service.service;

import com.hotelbooking.comments_service.document.HotelComment;
import com.hotelbooking.comments_service.dto.CommentStatsResponse;
import com.hotelbooking.comments_service.repository.HotelCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HotelCommentService {

    private final HotelCommentRepository hotelCommentRepository;

    public HotelCommentService(HotelCommentRepository hotelCommentRepository) {
        this.hotelCommentRepository = hotelCommentRepository;
    }

    public HotelComment addComment(HotelComment hotelComment) {
        return hotelCommentRepository.save(hotelComment);
    }

    public List<HotelComment> getCommentsByHotelId(Long hotelId) {
        return hotelCommentRepository.findByHotelId(hotelId);
    }

    public List<HotelComment> getAllComments() {
        return hotelCommentRepository.findAll();
    }

    public void deleteComment(String id) {
        hotelCommentRepository.deleteById(id);
    }

    public CommentStatsResponse getCommentStats(Long hotelId) {
        List<HotelComment> comments = hotelCommentRepository.findByHotelId(hotelId);

        if (comments.isEmpty()) {
            return new CommentStatsResponse(hotelId, 0.0, 0, Map.of());
        }

        double averageRating = comments.stream()
                .mapToInt(HotelComment::getRating)
                .average()
                .orElse(0.0);

        Map<Integer, Long> ratingDistribution = comments.stream()
                .collect(Collectors.groupingBy(
                        HotelComment::getRating,
                        Collectors.counting()
                ));

        return new CommentStatsResponse(
                hotelId,
                averageRating,
                comments.size(),
                ratingDistribution
        );
    }
}