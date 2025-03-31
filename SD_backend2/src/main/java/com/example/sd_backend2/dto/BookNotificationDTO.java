package com.example.sd_backend2.dto;

public class BookNotificationDTO {
    private String authorName;
    private String bookTitle;
    private Long bookId;

    public BookNotificationDTO(String authorName, String bookTitle, Long bookId) {
        this.authorName = authorName;
        this.bookTitle = bookTitle;
        this.bookId = bookId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
