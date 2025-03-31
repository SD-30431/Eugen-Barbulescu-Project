package com.example.sd_backend2.dto;

public class CategoryStatsDTO {
    private Long categoryId;
    private Long bookCount;

    public CategoryStatsDTO(Long categoryId, Long bookCount) {
        this.categoryId = categoryId;
        this.bookCount = bookCount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }
}
