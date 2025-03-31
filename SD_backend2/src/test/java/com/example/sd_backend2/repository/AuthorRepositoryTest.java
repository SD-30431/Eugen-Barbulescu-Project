package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testFindByName() {
        Author author = new Author("sampleUser", "pass", false);
        authorRepository.save(author);

        Author found = authorRepository.findByName("sampleUser");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("sampleUser");
    }
}
