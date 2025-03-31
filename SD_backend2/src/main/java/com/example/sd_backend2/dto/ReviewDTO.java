package com.example.sd_backend2.dto;

public class ReviewDTO {
    private Long reviewId;
    private Long bookId;
    private String bookTitle;
    private Long authorId;
    private String authorName;
    private int rating;

    public ReviewDTO() {
    }

    public ReviewDTO(Long reviewId, Long bookId, String bookTitle, Long authorId, String authorName, int rating) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.authorId = authorId;
        this.authorName = authorName;
        this.rating = rating;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
