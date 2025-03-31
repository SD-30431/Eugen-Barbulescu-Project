package com.example.sd_backend2.security;

import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Author author = authorRepository.findByName(username);
        if (author == null) {
            throw new UsernameNotFoundException("User not found with name: " + username);
        }
        return User.builder()
                .username(author.getName())
                .password(author.getPassword())
                .roles(author.isAdmin() ? "ADMIN" : "USER")
                .build();
    }
}
