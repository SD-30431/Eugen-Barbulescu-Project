package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.BookNotificationDTO;
import com.example.sd_backend2.dto.BookRequestDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Category;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private BookService bookService;

    private Author author;
    private Author follower;
    private Category category;
    private Book bookWithAuthor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create an author and assign a mutable set for followers
        author = new Author("user1", "pass", false);
        author.setAuthorId(10L);
        follower = new Author("follower", "pass", false);
        follower.setAuthorId(20L);
        author.setFollowers(new HashSet<>(Set.of(follower)));

        category = new Category("Fiction");
        category.setCategoryId(1L);

        // A book with an author must have its author set to avoid NPE in convertToDTO.
        bookWithAuthor = new Book();
        bookWithAuthor.setBookId(100L);
        bookWithAuthor.setTitle("Test Book");
        bookWithAuthor.setContent("Test Content");
        bookWithAuthor.setCategory(category);
        bookWithAuthor.setAuthor(author);
    }

    @Test
    void testCreateBook_Success() {
        String username = "user1";
        BookRequestDTO request = new BookRequestDTO();
        request.setTitle("Test Book");
        request.setContent("Test Content");
        request.setCategoryId(1L);

        // Use the previously created author and category from setUp()
        when(authorRepository.findByName(username)).thenReturn(author);
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(bookWithAuthor);

        BookRequestDTO result = bookService.createBook(request, username);

        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/notifications/" + follower.getAuthorId()), any(BookNotificationDTO.class));
        assertEquals(bookWithAuthor.getBookId(), result.getBookId());
        assertEquals(bookWithAuthor.getTitle(), result.getTitle());
        assertEquals(bookWithAuthor.getContent(), result.getContent());
        assertEquals(category.getCategoryId(), result.getCategoryId());
        assertEquals(category.getName(), result.getCategoryName());
        assertEquals(author.getAuthorId(), result.getAuthorId());
    }

    @Test
    void testGetBooks_All() {
        // Create two books with non-null authors
        Book book1 = new Book();
        book1.setBookId(1L);
        book1.setTitle("Book 1");
        book1.setContent("Content 1");
        // Use the author from setUp()
        book1.setAuthor(author);
        book1.setCategory(category);

        Book book2 = new Book();
        book2.setBookId(2L);
        book2.setTitle("Book 2");
        book2.setContent("Content 2");
        book2.setAuthor(author);
        book2.setCategory(category);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        List<BookRequestDTO> result = bookService.getBooks("all");
        assertEquals(2, result.size());
    }

    @Test
    void testGetBooks_ByCategory() {
        // Create a book with an author set
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Book Category");
        book.setContent("Content Category");
        book.setAuthor(author);
        book.setCategory(category);

        when(bookRepository.findByCategory_CategoryId(1L)).thenReturn(Arrays.asList(book));
        List<BookRequestDTO> result = bookService.getBooks("1");
        assertEquals(1, result.size());
    }

    @Test
    void testGetBookById_Found() {
        // Ensure the book has a non-null author
        Book book = new Book();
        book.setBookId(50L);
        book.setTitle("Found Book");
        book.setContent("Found Content");
        book.setAuthor(author);
        book.setCategory(category);

        when(bookRepository.findById(50L)).thenReturn(Optional.of(book));
        BookRequestDTO result = bookService.getBookById(50L);
        assertNotNull(result);
        assertEquals(50L, result.getBookId());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        BookRequestDTO result = bookService.getBookById(999L);
        assertNull(result);
    }

    @Test
    void testUpdateBook_Success() {
        String username = "user1";
        // Create a new author and category to simulate update.
        Author currentAuthor = new Author("user1", "pass", false);
        currentAuthor.setAuthorId(10L);

        Category oldCategory = new Category("Old");
        oldCategory.setCategoryId(1L);

        Book existingBook = new Book();
        existingBook.setBookId(100L);
        existingBook.setTitle("Old Title");
        existingBook.setContent("Old Content");
        existingBook.setCategory(oldCategory);
        existingBook.setAuthor(currentAuthor);

        BookRequestDTO updateRequest = new BookRequestDTO();
        updateRequest.setTitle("New Title");
        updateRequest.setContent("New Content");
        updateRequest.setCategoryId(2L); // new category id

        Category newCategory = new Category("New");
        newCategory.setCategoryId(2L);

        when(authorRepository.findByName(username)).thenReturn(currentAuthor);
        when(bookRepository.findById(100L)).thenReturn(Optional.of(existingBook));
        when(categoryService.getCategoryById(2L)).thenReturn(Optional.of(newCategory));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookRequestDTO result = bookService.updateBook(100L, updateRequest, username);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
        assertEquals(2L, result.getCategoryId());
        assertEquals("New", result.getCategoryName());
    }

    @Test
    void testDeleteBook_Success() {
        String username = "user1";
        Author currentAuthor = new Author("user1", "pass", false);
        currentAuthor.setAuthorId(10L);
        Book existingBook = new Book();
        existingBook.setBookId(200L);
        existingBook.setAuthor(currentAuthor);
        when(authorRepository.findByName(username)).thenReturn(currentAuthor);
        when(bookRepository.findById(200L)).thenReturn(Optional.of(existingBook));
        assertDoesNotThrow(() -> bookService.deleteBook(200L, username));
        verify(bookRepository, times(1)).delete(existingBook);
    }

    @Test
    void testDeleteBook_NotFound() {
        String username = "user1";
        when(authorRepository.findByName(username)).thenReturn(new Author("user1", "pass", false));
        when(bookRepository.findById(300L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookService.deleteBook(300L, username));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
