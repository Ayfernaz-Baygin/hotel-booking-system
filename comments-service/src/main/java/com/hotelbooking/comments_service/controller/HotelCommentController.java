package com.hotelbooking.comments_service.controller;

import com.hotelbooking.comments_service.document.HotelComment;
import com.hotelbooking.comments_service.dto.CommentStatsResponse;
import com.hotelbooking.comments_service.service.HotelCommentService;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class HotelCommentController {

    private final HotelCommentService hotelCommentService;

    public HotelCommentController(HotelCommentService hotelCommentService) {
        this.hotelCommentService = hotelCommentService;
    }

    @PostMapping
    public HotelComment addComment(@RequestBody HotelComment hotelComment) {
        return hotelCommentService.addComment(hotelComment);
    }

    @GetMapping("/hotel/{hotelId}")
    public List<HotelComment> getCommentsByHotelId(@PathVariable Long hotelId) {
        return hotelCommentService.getCommentsByHotelId(hotelId);
    }

    @GetMapping
    public List<HotelComment> getAllComments() {
        return hotelCommentService.getAllComments();
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable String id) {
        hotelCommentService.deleteComment(id);
        return "Comment deleted successfully!";
    }

    @GetMapping("/hotel/{hotelId}/stats")
   public CommentStatsResponse getCommentStats(@PathVariable Long hotelId) {
    return hotelCommentService.getCommentStats(hotelId);
  }
}