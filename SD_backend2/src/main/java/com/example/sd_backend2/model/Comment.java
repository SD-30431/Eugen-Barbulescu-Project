package com.example.sd_backend2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "bookId")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private Author author;

    @Column(length = 5000)
    private String commentStr;

    public Comment() {
    }

    public Comment(Book book, Author author, String commentStr) {
        this.book = book;
        this.author = author;
        this.commentStr = commentStr;
    }

    public Long getCommentId() {
        return commentId;
    }
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }
    public Author getAuthor() {
        return author;
    }
    public void setAuthor(Author author) {
        this.author = author;
    }
    public String getCommentStr() {
        return commentStr;
    }
    public void setCommentStr(String commentStr) {
        this.commentStr = commentStr;
    }
}
