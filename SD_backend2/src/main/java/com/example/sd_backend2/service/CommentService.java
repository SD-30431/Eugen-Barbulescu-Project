package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.CommentDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Comment;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.BookRepository;
import com.example.sd_backend2.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public CommentDTO createComment(Long bookId, String username, String commentStr) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found");
        }
        Author currentAuthor = authorRepository.findByName(username);
        Book book = optionalBook.get();
        Comment newComment = new Comment(book, currentAuthor, commentStr);
        Comment savedComment = commentRepository.save(newComment);
        return convertToDTO(savedComment);
    }

    public List<CommentDTO> getCommentsForBook(Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found");
        }
        List<Comment> comments = commentRepository.findByBook_BookId(bookId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        return convertToDTO(comment);
    }

    public CommentDTO updateComment(Long commentId, String username, String commentStr) {
        Author currentAuthor = authorRepository.findByName(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        if (!comment.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId()) && !currentAuthor.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this comment");
        }
        comment.setCommentStr(commentStr);
        Comment updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Long commentId, String username) {
        Author currentAuthor = authorRepository.findByName(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        if (!comment.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId()) && !currentAuthor.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this comment");
        }
        commentRepository.delete(comment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getCommentId(),
                comment.getBook().getBookId(),
                comment.getBook().getTitle(),
                comment.getAuthor().getAuthorId(),
                comment.getAuthor().getName(),
                comment.getCommentStr()
        );
    }
}
