package com.example.sd_backend2.dto;

public class CommentDTO {
    private Long commentId;
    private Long bookId;
    private String bookTitle;
    private Long authorId;
    private String authorName;
    private String commentStr;

    public CommentDTO() {
    }

    public CommentDTO(Long commentId, Long bookId, String bookTitle, Long authorId, String authorName, String commentStr) {
        this.commentId = commentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.authorId = authorId;
        this.authorName = authorName;
        this.commentStr = commentStr;
    }

    public Long getCommentId() {
        return commentId;
    }
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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
    public String getCommentStr() {
        return commentStr;
    }
    public void setCommentStr(String commentStr) {
        this.commentStr = commentStr;
    }
}
