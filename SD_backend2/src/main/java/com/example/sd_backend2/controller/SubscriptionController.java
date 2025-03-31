package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.SubscriptionDTO;
import com.example.sd_backend2.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/{subscribedToId}")
    public ResponseEntity<?> subscribe(@PathVariable Long subscribedToId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String message = subscriptionService.subscribe(subscribedToId, username);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{subscribedToId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long subscribedToId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String message = subscriptionService.unsubscribe(subscribedToId, username);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<?> getSubscriptions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptions(username);
        return ResponseEntity.ok(subscriptions);
    }
}
