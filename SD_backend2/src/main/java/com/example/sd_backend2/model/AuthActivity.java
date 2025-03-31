package com.example.sd_backend2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "authactivity")
public class AuthActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private Author author;

    private String activity;
    private LocalDateTime timestamp;

    public AuthActivity() {
    }

    public AuthActivity(Author author, String activity, LocalDateTime timestamp) {
        this.author = author;
        this.activity = activity;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
