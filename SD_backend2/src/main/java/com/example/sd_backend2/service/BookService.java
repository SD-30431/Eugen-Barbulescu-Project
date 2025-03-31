package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.BookNotificationDTO;
import com.example.sd_backend2.dto.BookRequestDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.model.Book;
import com.example.sd_backend2.model.Category;
import com.example.sd_backend2.repository.AuthorRepository;
import com.example.sd_backend2.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CategoryService categoryService;

    public BookRequestDTO createBook(BookRequestDTO bookRequest, String username) {
        Author currentAuthor = authorRepository.findByName(username);
        if (currentAuthor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        Book book = new Book();
        Optional<Category> category = categoryService.getCategoryById(bookRequest.getCategoryId());
        Category savedCategory = category.orElse(null);
        book.setAuthor(currentAuthor);
        book.setCategory(savedCategory);
        book.setTitle(bookRequest.getTitle());
        book.setContent(bookRequest.getContent());
        Book savedBook = bookRepository.save(book);
        Set<Author> followers = currentAuthor.getFollowers();
        for (Author follower : followers) {
            BookNotificationDTO notification = new BookNotificationDTO(currentAuthor.getName(), savedBook.getTitle(), savedBook.getBookId());
            messagingTemplate.convertAndSend("/topic/notifications/" + follower.getAuthorId(), notification);
        }
        return convertToDTO(savedBook);
    }

    public List<BookRequestDTO> getBooks(String category) {
        List<Book> books;
        if ("all".equalsIgnoreCase(category)) {
            books = bookRepository.findAll();
        } else {
            try {
                Long categoryId = Long.parseLong(category);
                books = bookRepository.findByCategory_CategoryId(categoryId);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category filter: " + category);
            }
        }
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookRequestDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public BookRequestDTO updateBook(Long id, BookRequestDTO updatedBook, String username) {
        Author currentAuthor = authorRepository.findByName(username);
        if (currentAuthor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (!existingBook.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId())
                            && !currentAuthor.isAdmin()) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to edit this book");
                    }
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setContent(updatedBook.getContent());
                    Optional<Category> optionalCategory = categoryService.getCategoryById(updatedBook.getCategoryId());
                    if(optionalCategory.isPresent()){
                        existingBook.setCategory(optionalCategory.get());
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category");
                    }
                    Book savedBook = bookRepository.save(existingBook);
                    return convertToDTO(savedBook);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    public void deleteBook(Long id, String username) {
        Author currentAuthor = authorRepository.findByName(username);
        if (currentAuthor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        bookRepository.findById(id)
                .ifPresentOrElse(book -> {
                    if (!book.getAuthor().getAuthorId().equals(currentAuthor.getAuthorId())
                            && !currentAuthor.isAdmin()) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this book");
                    }
                    bookRepository.delete(book);
                }, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
                });
    }

    private BookRequestDTO convertToDTO(Book book) {
        String authorName = (book.getAuthor() != null) ? book.getAuthor().getName() : "Unknown";
        return new BookRequestDTO(book.getBookId(), book.getTitle(), book.getAuthor().getAuthorId(), authorName, book.getCategory().getCategoryId(), book.getCategory().getName(), book.getContent());
    }
}
