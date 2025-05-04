package com.example.sd_backend2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // “username” was put into attributes by your HandshakeInterceptor
        String username = (String) session.getAttributes().get("username");
        sessions.put(username, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("username");
        sessions.remove(username);
    }

    // We don’t expect inbound messages, so no handleTextMessage override

    /**
     * Send a typed DTO to a single user as JSON.
     */
    public void sendToUser(String username, Object dto) {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            try {
                String payload = mapper.writeValueAsString(dto);
                session.sendMessage(new TextMessage(payload));
            } catch (Exception e) {
                // log or handle
                e.printStackTrace();
            }
        }
    }
}
