package com.example.sd_backend2.controller;

import com.example.sd_backend2.model.Category;
import com.example.sd_backend2.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.example.sd_backend2.security.JwtAuthenticationFilter.class})
)
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser
    public void testGetAllCategories() throws Exception {
        Category category = new Category("Fiction");
        category.setCategoryId(1L);
        Mockito.when(categoryService.getAllCategories())
                .thenReturn(Collections.singletonList(category));

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[0].name").value("Fiction"));
    }

    @Test
    @WithMockUser
    public void testGetCategoryById() throws Exception {
        Category category = new Category("Non-fiction");
        category.setCategoryId(2L);
        Mockito.when(categoryService.getCategoryById(2L))
                .thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/categories/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(2))
                .andExpect(jsonPath("$.name").value("Non-fiction"));
    }

    @Test
    @WithMockUser
    public void testGetCategoryByName() throws Exception {
        Category category = new Category("Science");
        category.setCategoryId(3L);
        Mockito.when(categoryService.getCategoryByName("Science"))
                .thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/categories/name/Science")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(3))
                .andExpect(jsonPath("$.name").value("Science"));
    }
}
