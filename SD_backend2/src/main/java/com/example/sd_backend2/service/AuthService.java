package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.AuthResponseDTO;
import com.example.sd_backend2.dto.LoginRequestDTO;
import com.example.sd_backend2.dto.SignupRequestDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.AuthActivity;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.AuthActivityRepository;
import com.example.sd_backend2.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthActivityRepository authActivityRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> signup(SignupRequestDTO request) {
        if (authorRepository.findByName(request.name) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        boolean isAdmin = false;
        String hashedPassword = passwordEncoder.encode(request.password);

        Author author = new Author(request.name, hashedPassword, isAdmin);
        authorRepository.save(author);

        return ResponseEntity.ok("Signup successful");
    }

    public ResponseEntity<?> login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.name, request.password)
            );

            Author author = authorRepository.findByName(request.name);
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(author.getName())
                    .password(author.getPassword())
                    .roles(author.isAdmin() ? "ADMIN" : "USER")
                    .build();

            String token = jwtTokenUtil.generateToken(userDetails);

            AuthActivity activity = new AuthActivity(author, "login", LocalDateTime.now());
            authActivityRepository.save(activity);

            String role = author.isAdmin() ? "ADMIN" : "USER";
            return ResponseEntity.ok(new AuthResponseDTO(token, author.getAuthorId(), role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    public ResponseEntity<?> logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);

            Author author = authorRepository.findByName(username);
            if (author != null) {
                AuthActivity activity = new AuthActivity(author, "logout", LocalDateTime.now());
                authActivityRepository.save(activity);
            }
        }
        return ResponseEntity.ok("Logout recorded");
    }
}
