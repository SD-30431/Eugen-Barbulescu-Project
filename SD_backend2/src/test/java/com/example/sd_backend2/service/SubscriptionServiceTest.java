package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.SubscriptionDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Author subscriber;
    private Author subscribedTo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subscriber = new Author("user1", "pass", false);
        subscriber.setAuthorId(10L);
        // Use a mutable set instead of an immutable one:
        subscriber.setFollowing(new HashSet<>());

        subscribedTo = new Author("otherUser", "pass", false);
        subscribedTo.setAuthorId(20L);
        subscribedTo.setFollowers(new HashSet<>());
    }

    @Test
    void testSubscribe_Success() {
        when(authorRepository.findByName("user1")).thenReturn(subscriber);
        when(authorRepository.findById(20L)).thenReturn(Optional.of(subscribedTo));
        // Initially, subscriber is not following anyone.
        String result = subscriptionService.subscribe(20L, "user1");
        assertEquals("Subscription successful", result);
        // Verify that both subscriber and subscribedTo have been updated
        assertTrue(subscriber.getFollowing().contains(subscribedTo));
        assertTrue(subscribedTo.getFollowers().contains(subscriber));
        verify(authorRepository, times(2)).save(any(Author.class));
    }

    @Test
    void testSubscribe_SelfSubscription() {
        when(authorRepository.findByName("user1")).thenReturn(subscriber);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> subscriptionService.subscribe(10L, "user1"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testSubscribe_AlreadySubscribed() {
        // Add subscribedTo to subscriber's following using a mutable set
        subscriber.getFollowing().add(subscribedTo);
        when(authorRepository.findByName("user1")).thenReturn(subscriber);
        when(authorRepository.findById(20L)).thenReturn(Optional.of(subscribedTo));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> subscriptionService.subscribe(20L, "user1"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testUnsubscribe_Success() {
        // Ensure mutable sets for following and followers
        subscriber.setFollowing(new HashSet<>());
        subscribedTo.setFollowers(new HashSet<>());
        subscriber.getFollowing().add(subscribedTo);
        subscribedTo.getFollowers().add(subscriber);
        when(authorRepository.findByName("user1")).thenReturn(subscriber);
        when(authorRepository.findById(20L)).thenReturn(Optional.of(subscribedTo));

        String result = subscriptionService.unsubscribe(20L, "user1");
        assertEquals("Unsubscribed successfully", result);
        assertFalse(subscriber.getFollowing().contains(subscribedTo));
        assertFalse(subscribedTo.getFollowers().contains(subscriber));
        verify(authorRepository, times(2)).save(any(Author.class));
    }

    @Test
    void testUnsubscribe_NotSubscribed() {
        subscriber.setFollowing(new HashSet<>());
        when(authorRepository.findByName("user1")).thenReturn(subscriber);
        when(authorRepository.findById(20L)).thenReturn(Optional.of(subscribedTo));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> subscriptionService.unsubscribe(20L, "user1"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

}
