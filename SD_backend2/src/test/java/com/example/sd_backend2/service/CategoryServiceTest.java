package com.example.sd_backend2.service;

import com.example.sd_backend2.model.Category;
import com.example.sd_backend2.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCategoryById_Found() {
        Category category = new Category("Fiction");
        category.setCategoryId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Optional<Category> result = categoryService.getCategoryById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getCategoryId());
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Category> result = categoryService.getCategoryById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCategoryByName_Found() {
        Category category = new Category("Non-fiction");
        category.setCategoryId(2L);
        when(categoryRepository.findByName("Non-fiction")).thenReturn(Optional.of(category));
        Optional<Category> result = categoryService.getCategoryByName("Non-fiction");
        assertTrue(result.isPresent());
        assertEquals("Non-fiction", result.get().getName());
    }

    @Test
    void testGetAllCategories() {
        Category cat1 = new Category("Fiction");
        cat1.setCategoryId(1L);
        Category cat2 = new Category("Science");
        cat2.setCategoryId(2L);
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));
        List<Category> result = categoryService.getAllCategories();
        assertEquals(2, result.size());
    }
}
