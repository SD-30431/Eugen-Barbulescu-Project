package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.CommentDTO;
import com.example.sd_backend2.dto.CommentRequestDTO;
import com.example.sd_backend2.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/books/{bookId}")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long bookId, @RequestBody CommentRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CommentDTO newComment = commentService.createComment(bookId, username, request.getCommentString());
        return ResponseEntity.ok(newComment);
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<CommentDTO>> getCommentsForBook(@PathVariable Long bookId) {
        List<CommentDTO> comments = commentService.getCommentsForBook(bookId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long commentId) {
        CommentDTO comment = commentService.getComment(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CommentDTO updatedComment = commentService.updateComment(commentId, username, request.getCommentString());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.deleteComment(commentId, username);
        return ResponseEntity.ok("Comment deleted");
    }
}
