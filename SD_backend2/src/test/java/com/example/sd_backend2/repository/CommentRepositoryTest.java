package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindByBook_BookId() {
        Author author = new Author("commenter", "pass", false);
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        entityManager.persist(book);

        Comment comment1 = new Comment(book, author, "Nice!");
        Comment comment2 = new Comment(book, author, "Great read!");
        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.flush();

        List<Comment> comments = commentRepository.findByBook_BookId(book.getBookId());
        assertThat(comments).hasSize(2);
    }
}
