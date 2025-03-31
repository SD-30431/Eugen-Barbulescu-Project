package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.AuthActivityDTO;
import com.example.sd_backend2.dto.CategoryStatsDTO;
import com.example.sd_backend2.service.AdminService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.example.sd_backend2.security.JwtAuthenticationFilter.class})
)
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetRecentActivity() throws Exception {
        AuthActivityDTO activity = new AuthActivityDTO(
                1L,
                10L,
                "LOGIN",
                LocalDateTime.of(2025, 3, 16, 10, 0)
        );
        List<AuthActivityDTO> activities = Collections.singletonList(activity);

        Mockito.when(adminService.getRecentActivity()).thenReturn(activities);

        mockMvc.perform(get("/api/admin/activity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].authorId").value(10))
                .andExpect(jsonPath("$[0].activity").value("LOGIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetCategoryStats() throws Exception {
        // Assuming CategoryStatsDTO has properties: categoryId and bookCount.
        CategoryStatsDTO stat = new CategoryStatsDTO(1L, 5L);
        List<CategoryStatsDTO> stats = Collections.singletonList(stat);

        Mockito.when(adminService.getCategoryStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[0].bookCount").value(5));
    }
}
