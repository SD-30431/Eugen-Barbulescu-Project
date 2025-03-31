package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.BookRequestDTO;
import com.example.sd_backend2.service.BookService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.example.sd_backend2.security.JwtAuthenticationFilter.class})
)
@ActiveProfiles("test")
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    public void testCreateBook() throws Exception {
        BookRequestDTO request = new BookRequestDTO();
        request.setTitle("Test Book");
        // Set additional fields as needed

        BookRequestDTO createdBook = new BookRequestDTO(1L, "Test Book", 1L, "Author", 1L, "Category", "Content");

        Mockito.when(bookService.createBook(any(BookRequestDTO.class), eq("testUser")))
                .thenReturn(createdBook);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser
    public void testGetBooks() throws Exception {
        BookRequestDTO book = new BookRequestDTO(1L, "Test Book", 1L, "Author", 1L, "Category", "Content");
        List<BookRequestDTO> books = Collections.singletonList(book);

        Mockito.when(bookService.getBooks("all")).thenReturn(books);

        mockMvc.perform(get("/api/books?category=all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser
    public void testGetBook() throws Exception {
        BookRequestDTO book = new BookRequestDTO(1L, "Test Book", 1L, "Author", 1L, "Category", "Content");

        Mockito.when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUpdateBook() throws Exception {
        BookRequestDTO updatedBook = new BookRequestDTO(1L, "Updated Book", 1L, "Author", 1L, "Category", "Content");

        Mockito.when(bookService.updateBook(eq(1L), any(BookRequestDTO.class), eq("testUser")))
                .thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testDeleteBook() throws Exception {
        Mockito.doNothing().when(bookService).deleteBook(1L, "testUser");

        mockMvc.perform(delete("/api/books/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted"));
    }
}
