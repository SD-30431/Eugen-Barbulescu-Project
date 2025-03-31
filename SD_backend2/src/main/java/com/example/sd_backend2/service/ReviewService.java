package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.ReviewDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Review;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.BookRepository;
import com.example.sd_backend2.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public ReviewDTO createReview(Long bookId, String username, int reviewRating) {
        if (reviewRating < 1 || reviewRating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review must be between 1 and 5");
        }
        Author currentAuthor = authorRepository.findByName(username);
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found");
        }
        Book book = optionalBook.get();
        if (book.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot review your own book");
        }
        Optional<Review> existingReview = reviewRepository.findByBook_BookIdAndAuthor_AuthorId(bookId, currentAuthor.getAuthorId());
        if (existingReview.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reviewed this book");
        }
        Review newReview = new Review(book, currentAuthor, reviewRating);
        Review savedReview = reviewRepository.save(newReview);
        return convertToDTO(savedReview);
    }

    public List<ReviewDTO> getReviewsForBook(Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found");
        }
        List<Review> reviews = reviewRepository.findByBook_BookId(bookId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        return convertToDTO(review);
    }

    public ReviewDTO updateReview(Long reviewId, String username, int reviewRating) {
        if (reviewRating < 1 || reviewRating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review must be between 1 and 5");
        }
        Author currentAuthor = authorRepository.findByName(username);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!review.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId()) && !currentAuthor.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this review");
        }
        review.setReview(reviewRating);
        Review updatedReview = reviewRepository.save(review);
        return convertToDTO(updatedReview);
    }

    public void deleteReview(Long reviewId, String username) {
        Author currentAuthor = authorRepository.findByName(username);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!review.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId()) && !currentAuthor.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this review");
        }
        reviewRepository.delete(review);
    }

    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getReviewId(),
                review.getBook().getBookId(),
                review.getBook().getTitle(),
                review.getAuthor().getAuthorId(),
                review.getAuthor().getName(),
                review.getReview()
        );
    }
}
