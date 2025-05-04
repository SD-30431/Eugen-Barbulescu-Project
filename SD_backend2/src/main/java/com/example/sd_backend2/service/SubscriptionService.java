package com.example.sd_backend2.service;

import com.example.sd_backend2.dto.SubscriberDTO;
import com.example.sd_backend2.dto.SubscriptionDTO;
import com.example.sd_backend2.model.Author;
import com.example.sd_backend2.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private AuthorRepository authorRepository;

    public String subscribe(Long subscribedToId, String username) {
        Author subscriber = authorRepository.findByName(username);
        if (subscriber == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Subscriber not found");
        }
        if (subscriber.getAuthorId().equals(subscribedToId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot subscribe to yourself");
        }
        Author subscribedTo = authorRepository.findById(subscribedToId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author to subscribe to not found"));
        if (subscriber.getFollowing().contains(subscribedTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already subscribed");
        }
        subscriber.getFollowing().add(subscribedTo);
        subscribedTo.getFollowers().add(subscriber);
        authorRepository.save(subscriber);
        authorRepository.save(subscribedTo);
        return "Subscription successful";
    }

    public String unsubscribe(Long subscribedToId, String username) {
        Author subscriber = authorRepository.findByName(username);
        if (subscriber == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Subscriber not found");
        }
        Author subscribedTo = authorRepository.findById(subscribedToId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author to unsubscribe from not found"));
        if (!subscriber.getFollowing().contains(subscribedTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not subscribed");
        }
        subscriber.getFollowing().remove(subscribedTo);
        subscribedTo.getFollowers().remove(subscriber);
        authorRepository.save(subscriber);
        authorRepository.save(subscribedTo);
        return "Unsubscribed successfully";
    }

    public List<SubscriptionDTO> getSubscriptions(String username) {
        Author subscriber = authorRepository.findByName(username);
        if (subscriber == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Subscriber not found");
        }
        return subscriber.getFollowing()
                .stream()
                .map(author -> new SubscriptionDTO(author.getAuthorId(), author.getName()))
                .collect(Collectors.toList());
    }

    public List<SubscriberDTO> getSubscribers(String username) {
        Author author = authorRepository.findByName(username);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        }

        Set<Author> followers = author.getFollowers();

        return followers.stream()
                .map(follower -> new SubscriberDTO(follower.getAuthorId(), follower.getName()))
                .collect(Collectors.toList());
    }

}
