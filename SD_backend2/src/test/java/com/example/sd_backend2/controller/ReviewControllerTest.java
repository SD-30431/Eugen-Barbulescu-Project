package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.ReviewDTO;
import com.example.sd_backend2.dto.ReviewRequestDTO;
import com.example.sd_backend2.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.example.sd_backend2.security.JwtAuthenticationFilter.class})
)
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    public void testCreateReview() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setReview(2);
        ReviewDTO review = new ReviewDTO(1L, 1L, "Book Title", 1L, "testUser", 5);

        Mockito.when(reviewService.createReview(eq(1L), eq("testUser"), any(Integer.class)))
                .thenReturn(review);

        mockMvc.perform(post("/api/reviews/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @WithMockUser
    public void testGetReviewsForBook() throws Exception {
        ReviewDTO review = new ReviewDTO(2L, 1L, "Book Title", 1L, "testUser", 4);
        List<ReviewDTO> reviews = Collections.singletonList(review);

        Mockito.when(reviewService.getReviewsForBook(1L)).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(2))
                .andExpect(jsonPath("$[0].rating").value(4));
    }

    @Test
    @WithMockUser
    public void testGetReview() throws Exception {
        ReviewDTO review = new ReviewDTO(3L, 1L, "Book Title", 1L, "testUser", 3);
        Mockito.when(reviewService.getReview(3L)).thenReturn(review);

        mockMvc.perform(get("/api/reviews/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(3))
                .andExpect(jsonPath("$.rating").value(3));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateReview() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setReview(5);
        ReviewDTO updatedReview = new ReviewDTO(4L, 1L, "Book Title", 1L, "testUser", 4);

        Mockito.when(reviewService.updateReview(eq(4L), eq("testUser"), any(Integer.class)))
                .thenReturn(updatedReview);

        mockMvc.perform(put("/api/reviews/4")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testDeleteReview() throws Exception {
        Mockito.doNothing().when(reviewService).deleteReview(5L, "testUser");

        mockMvc.perform(delete("/api/reviews/5")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted"));
    }
}
