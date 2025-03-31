package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor_AuthorId(Long authorId);
    List<Book> findByCategory_CategoryId(Long categoryId);

    @Query("SELECT b.category.categoryId, COUNT(b) FROM Book b GROUP BY b.category.categoryId")
    List<Object[]> countBooksByCategory();
}
