package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.AuthActivityDTO;
import com.example.sd_backend2.dto.CategoryStatsDTO;
import com.example.sd_backend2.model.AuthActivity;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.repository.AuthActivityRepository;
import com.example.sd_backend2.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AuthActivityRepository authActivityRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRecentActivity() {
        Author mockAuthor = new Author("testUser", "hashedPass", false);
        mockAuthor.setAuthorId(10L);

        AuthActivity activity1 = new AuthActivity(mockAuthor, "login", LocalDateTime.now());
        activity1.setId(1L);
        AuthActivity activity2 = new AuthActivity(mockAuthor, "logout", LocalDateTime.now());
        activity2.setId(2L);

        when(authActivityRepository.findTop100ByOrderByTimestampDesc())
                .thenReturn(Arrays.asList(activity1, activity2));

        List<AuthActivityDTO> result = adminService.getRecentActivity();

        assertEquals(2, result.size());
        assertEquals(activity1.getId(), result.get(0).getId());
        assertEquals(activity1.getAuthor().getAuthorId(), result.get(0).getAuthorId());
        assertEquals(activity1.getActivity(), result.get(0).getActivity());
        assertEquals(activity2.getId(), result.get(1).getId());
    }

    @Test
    void testGetCategoryStats() {
        Object[] row1 = {1L, 5L};
        Object[] row2 = {2L, 10L};

        when(bookRepository.countBooksByCategory()).thenReturn(Arrays.asList(row1, row2));

        List<CategoryStatsDTO> result = adminService.getCategoryStats();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCategoryId());
        assertEquals(5L, result.get(0).getBookCount());
        assertEquals(2L, result.get(1).getCategoryId());
        assertEquals(10L, result.get(1).getBookCount());
    }
}
