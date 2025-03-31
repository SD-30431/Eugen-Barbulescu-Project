package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void cleanUp() {
        // Delete dependent entities first.
        entityManager.getEntityManager().createQuery("DELETE FROM Review").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Comment").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Book").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Category").executeUpdate();
    }

    @Test
    void testFindByAuthor_AuthorId() {
        Author author = new Author("authorTest", "pass", false);
        entityManager.persist(author);

        Book book1 = new Book();
        book1.setTitle("Book One");
        book1.setAuthor(author);
        entityManager.persist(book1);
        entityManager.flush();

        List<Book> books = bookRepository.findByAuthor_AuthorId(author.getAuthorId());
        assertThat(books).hasSize(1);
    }

    @Test
    void testFindByCategory_CategoryId() {
        Category category = new Category("Sci-Fi");
        entityManager.persist(category);

        Book book = new Book();
        book.setTitle("Space Adventures");
        book.setCategory(category);
        // Ensure an author is set.
        Author author = new Author("author", "pass", false);
        entityManager.persist(author);
        book.setAuthor(author);
        entityManager.persist(book);
        entityManager.flush();

        List<Book> books = bookRepository.findByCategory_CategoryId(category.getCategoryId());
        assertThat(books).hasSize(1);
    }

    @Test
    void testCountBooksByCategory() {
        Category category = new Category("Fantasy");
        entityManager.persist(category);

        // Create two books in the same category
        Book book1 = new Book();
        book1.setTitle("Fantasy Book 1");
        book1.setCategory(category);
        Author author = new Author("auth", "pass", false);
        entityManager.persist(author);
        book1.setAuthor(author);
        entityManager.persist(book1);

        Book book2 = new Book();
        book2.setTitle("Fantasy Book 2");
        book2.setCategory(category);
        book2.setAuthor(author);
        entityManager.persist(book2);

        entityManager.flush();

        List<Object[]> stats = bookRepository.countBooksByCategory();
        assertThat(stats).isNotEmpty();
        Object[] row = stats.get(0);
        Long catId = ((Number) row[0]).longValue();
        Long count = ((Number) row[1]).longValue();
        assertThat(catId).isEqualTo(category.getCategoryId());
        assertThat(count).isEqualTo(2L);
    }
}
