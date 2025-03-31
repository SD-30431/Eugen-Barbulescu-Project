package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.CommentDTO;
import com.example.sd_backend2.dto.CommentRequestDTO;
import com.example.sd_backend2.service.CommentService;
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

@WebMvcTest(controllers = CommentController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.example.sd_backend2.security.JwtAuthenticationFilter.class})
)
@ActiveProfiles("test")
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    public void testCreateComment() throws Exception {
        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentString("Great book!");

        CommentDTO response = new CommentDTO(1L, 1L, "Book Title", 1L, "testUser", "Great book!");

        Mockito.when(commentService.createComment(eq(1L), eq("testUser"), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/comments/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.commentStr").value("Great book!"));
    }

    @Test
    @WithMockUser
    public void testGetCommentsForBook() throws Exception {
        CommentDTO comment = new CommentDTO(2L, 1L, "Book Title", 1L, "testUser", "Nice read!");
        List<CommentDTO> comments = Collections.singletonList(comment);

        Mockito.when(commentService.getCommentsForBook(1L)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(2))
                .andExpect(jsonPath("$[0].commentStr").value("Nice read!"));
    }

    @Test
    @WithMockUser
    public void testGetComment() throws Exception {
        CommentDTO comment = new CommentDTO(3L, 1L, "Book Title", 1L, "testUser", "Interesting!");
        Mockito.when(commentService.getComment(3L)).thenReturn(comment);

        mockMvc.perform(get("/api/comments/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(3))
                .andExpect(jsonPath("$.commentStr").value("Interesting!"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateComment() throws Exception {
        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentString("Updated comment");
        CommentDTO updatedComment = new CommentDTO(4L, 1L, "Book Title", 1L, "testUser", "Updated comment");

        Mockito.when(commentService.updateComment(eq(4L), eq("testUser"), any(String.class)))
                .thenReturn(updatedComment);

        mockMvc.perform(put("/api/comments/4")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentStr").value("Updated comment"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testDeleteComment() throws Exception {
        Mockito.doNothing().when(commentService).deleteComment(5L, "testUser");

        mockMvc.perform(delete("/api/comments/5")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment deleted"));
    }
}
