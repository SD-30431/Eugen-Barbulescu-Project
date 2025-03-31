package com.example.sd_backend2.dto;

public class CommentRequestDTO {
    private String commentString;

    public CommentRequestDTO() {
    }

    public CommentRequestDTO(String commentString) {
        this.commentString = commentString;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }
}
