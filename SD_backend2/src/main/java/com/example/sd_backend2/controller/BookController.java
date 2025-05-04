package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.BookRequestDTO;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.service.BookService; // Import the BookService
import com.example.sd_backend2.service.NotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @PostMapping
    public ResponseEntity<BookRequestDTO> createBook(@RequestBody BookRequestDTO bookRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        BookRequestDTO createdBook = bookService.createBook(bookRequest, username);

        Long authorId = authorRepository.findByName(username).getAuthorId();
        return ResponseEntity.ok(createdBook);
    }

    @GetMapping
    public List<BookRequestDTO> getBooks(@RequestParam(value = "category", defaultValue = "all") String category) {
        return bookService.getBooks(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookRequestDTO> getBook(@PathVariable Long id) {
        BookRequestDTO bookRequestDTO = bookService.getBookById(id);
        if (bookRequestDTO != null) {
            return ResponseEntity.ok(bookRequestDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookRequestDTO> updateBook(@PathVariable Long id, @RequestBody BookRequestDTO updatedBook) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        try {
            BookRequestDTO updatedBookRequestDTO = bookService.updateBook(id, updatedBook, username);
            return ResponseEntity.ok(updatedBookRequestDTO);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        try {
            bookService.deleteBook(id, username);
            return ResponseEntity.ok("Book deleted");
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }


    @GetMapping(value = "/{id}/export", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<BookRequestDTO> exportBookAsXml(@PathVariable Long id) {
        BookRequestDTO dto = bookService.getBookById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}
