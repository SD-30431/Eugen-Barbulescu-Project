package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testFindByName() {
        Category category = new Category("History");
        categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByName("History");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("History");
    }
}
