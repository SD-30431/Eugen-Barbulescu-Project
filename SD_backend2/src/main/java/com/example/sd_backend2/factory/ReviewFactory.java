package com.example.sd_backend2.factory;

import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewFactory {
    public Review create(Book book, Author author, int rating) {
        return new Review(book, author, rating);
    }
}
