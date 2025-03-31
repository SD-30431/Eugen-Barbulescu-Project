package com.example.sd_backend2.dto;

public class AuthResponseDTO {
    public String token;
    public Long authorId;
    public String role;

    public AuthResponseDTO(String token, Long authorId, String role) {
        this.token = token;
        this.authorId = authorId;
        this.role = role;
    }
}
