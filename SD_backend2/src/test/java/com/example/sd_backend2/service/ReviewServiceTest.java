package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.ReviewDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Review;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.BookRepository;
import com.example.sd_backend2.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Author reviewer;
    private Author bookAuthor;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewer = new Author("user1", "pass", false);
        reviewer.setAuthorId(10L);
        bookAuthor = new Author("bookAuthor", "pass", false);
        bookAuthor.setAuthorId(20L);
        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setAuthor(bookAuthor); // ensure book author is set
    }

    @Test
    void testCreateReview_Success() {
        int rating = 4;
        when(authorRepository.findByName("user1")).thenReturn(reviewer);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findByBook_BookIdAndAuthor_AuthorId(1L, 10L)).thenReturn(Optional.empty());
        Review review = new Review(book, reviewer, rating);
        review.setReviewId(100L);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO result = reviewService.createReview(1L, "user1", rating);
        assertNotNull(result);
        assertEquals(100L, result.getReviewId());
        assertEquals(1L, result.getBookId());
        assertEquals("Test Book", result.getBookTitle());
        assertEquals(10L, result.getAuthorId());
        assertEquals("user1", result.getAuthorName());
        assertEquals(rating, result.getRating());
    }

    @Test
    void testCreateReview_InvalidRating() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(1L, "user1", 0));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testCreateReview_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(1L, "user1", 3));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testGetReviewsForBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Review review1 = new Review(book, reviewer, 4);
        review1.setReviewId(101L);
        Review review2 = new Review(book, reviewer, 5);
        review2.setReviewId(102L);
        when(reviewRepository.findByBook_BookId(1L)).thenReturn(Arrays.asList(review1, review2));

        List<ReviewDTO> reviews = reviewService.getReviewsForBook(1L);
        assertEquals(2, reviews.size());
    }

    @Test
    void testGetReview_Found() {
        Review review = new Review(book, reviewer, 4);
        review.setReviewId(200L);
        when(reviewRepository.findById(200L)).thenReturn(Optional.of(review));

        ReviewDTO result = reviewService.getReview(200L);
        assertNotNull(result);
        assertEquals(200L, result.getReviewId());
    }

    @Test
    void testUpdateReview_Success() {
        int newRating = 5;
        Review existingReview = new Review(book, reviewer, 3);
        existingReview.setReviewId(300L);
        when(authorRepository.findByName("user1")).thenReturn(reviewer);
        when(reviewRepository.findById(300L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewDTO updated = reviewService.updateReview(300L, "user1", newRating);
        assertNotNull(updated);
        assertEquals(newRating, updated.getRating());
    }

    @Test
    void testDeleteReview_Success() {
        Review existingReview = new Review(book, reviewer, 4);
        existingReview.setReviewId(400L);
        when(authorRepository.findByName("user1")).thenReturn(reviewer);
        when(reviewRepository.findById(400L)).thenReturn(Optional.of(existingReview));

        assertDoesNotThrow(() -> reviewService.deleteReview(400L, "user1"));
        verify(reviewRepository, times(1)).delete(existingReview);
    }
}
