package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.CommentDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Comment;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.BookRepository;
import com.example.sd_backend2.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment_Success() {
        Long bookId = 1L;
        String username = "user1";
        String commentStr = "Great book!";

        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle("Test Book");

        Author author = new Author("user1", "pass", false);
        author.setAuthorId(10L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findByName(username)).thenReturn(author);
        Comment savedComment = new Comment(book, author, commentStr);
        savedComment.setCommentId(100L);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDTO result = commentService.createComment(bookId, username, commentStr);
        assertEquals(100L, result.getCommentId());
        assertEquals(bookId, result.getBookId());
        assertEquals("Test Book", result.getBookTitle());
        assertEquals(10L, result.getAuthorId());
        assertEquals("user1", result.getAuthorName());
        assertEquals(commentStr, result.getCommentStr());
    }

    @Test
    void testGetCommentsForBook_Success() {
        Long bookId = 1L;
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle("Test Book");

        Author author = new Author("user1", "pass", false);
        author.setAuthorId(10L);

        Comment comment1 = new Comment(book, author, "Nice!");
        comment1.setCommentId(100L);
        Comment comment2 = new Comment(book, author, "Well done!");
        comment2.setCommentId(101L);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(commentRepository.findByBook_BookId(bookId)).thenReturn(Arrays.asList(comment1, comment2));

        List<CommentDTO> result = commentService.getCommentsForBook(bookId);
        assertEquals(2, result.size());
    }

    @Test
    void testGetComment_Found() {
        Long commentId = 200L;
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");

        Author author = new Author("user1", "pass", false);
        author.setAuthorId(10L);

        Comment comment = new Comment(book, author, "Awesome!");
        comment.setCommentId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        CommentDTO result = commentService.getComment(commentId);
        assertEquals(commentId, result.getCommentId());
    }

    @Test
    void testUpdateComment_Success() {
        Long commentId = 300L;
        String username = "user1";
        String newCommentStr = "Updated comment";

        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");

        Author author = new Author("user1", "pass", false);
        author.setAuthorId(10L);

        Comment existingComment = new Comment(book, author, "Old comment");
        existingComment.setCommentId(commentId);

        when(authorRepository.findByName(username)).thenReturn(author);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentDTO result = commentService.updateComment(commentId, username, newCommentStr);
        assertEquals(newCommentStr, result.getCommentStr());
    }

    @Test
    void testDeleteComment_Success() {
        Long commentId = 400L;
        String username = "user1";

        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");

        Author author = new Author("user1", "pass", false);
        author.setAuthorId(10L);

        Comment existingComment = new Comment(book, author, "To be deleted");
        existingComment.setCommentId(commentId);

        when(authorRepository.findByName(username)).thenReturn(author);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        assertDoesNotThrow(() -> commentService.deleteComment(commentId, username));
        verify(commentRepository, times(1)).delete(existingComment);
    }

    @Test
    void testDeleteComment_NotFound() {
        Long commentId = 500L;
        String username = "user1";
        when(authorRepository.findByName(username)).thenReturn(new Author("user1", "pass", false));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.deleteComment(commentId, username));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
