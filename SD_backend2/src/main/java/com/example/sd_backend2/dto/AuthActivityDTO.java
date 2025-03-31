package com.example.sd_backend2.dto;

import java.time.LocalDateTime;

public class AuthActivityDTO {
    private Long id;
    private Long authorId;
    private String activity;
    private LocalDateTime timestamp;

    public AuthActivityDTO(Long id, Long authorId, String activity, LocalDateTime timestamp) {
        this.id = id;
        this.authorId = authorId;
        this.activity = activity;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getActivity() {
        return activity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
