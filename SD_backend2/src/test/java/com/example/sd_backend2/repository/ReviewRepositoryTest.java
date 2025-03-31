package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void cleanUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM Review").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Book").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Author").executeUpdate();
    }

    @Test
    void testFindByBook_BookId() {
        Author author = new Author("reviewer", "pass", false);
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Review Test Book");
        book.setAuthor(author);
        entityManager.persist(book);

        Review review1 = new Review(book, author, 4);
        Review review2 = new Review(book, author, 5);
        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.flush();

        List<Review> reviews = reviewRepository.findByBook_BookId(book.getBookId());
        assertThat(reviews).hasSize(2);
    }

    @Test
    void testFindByBook_BookIdAndAuthor_AuthorId() {
        Author reviewer = new Author("reviewer", "pass", false);
        entityManager.persist(reviewer);

        Book book = new Book();
        book.setTitle("Review Specific Book");
        Author bookAuthor = new Author("bookAuthor", "pass", false);
        entityManager.persist(bookAuthor);
        book.setAuthor(bookAuthor);
        entityManager.persist(book);

        Review review = new Review(book, reviewer, 4);
        entityManager.persist(review);
        entityManager.flush();

        Optional<Review> found = reviewRepository.findByBook_BookIdAndAuthor_AuthorId(book.getBookId(), reviewer.getAuthorId());
        assertThat(found).isPresent();
        assertThat(found.get().getReview()).isEqualTo(4);
    }
}
