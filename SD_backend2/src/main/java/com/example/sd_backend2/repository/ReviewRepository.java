package com.example.sd_backend2.repository;

import com.example.sd_backend2.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook_BookId(Long bookId);
    Optional<Review> findByBook_BookIdAndAuthor_AuthorId(Long bookId, Long authorId);
}
