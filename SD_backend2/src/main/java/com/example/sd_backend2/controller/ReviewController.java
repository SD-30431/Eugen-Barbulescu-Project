package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.ReviewDTO;
import com.example.sd_backend2.dto.ReviewRequestDTO;
import com.example.sd_backend2.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/books/{bookId}")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable Long bookId, @RequestBody ReviewRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO newReview = reviewService.createReview(bookId, username, request.getReview());
        return ResponseEntity.ok(newReview);
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForBook(@PathVariable Long bookId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForBook(bookId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, username, request.getReview());
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewService.deleteReview(reviewId, username);
        return ResponseEntity.ok("Review deleted");
    }
}
