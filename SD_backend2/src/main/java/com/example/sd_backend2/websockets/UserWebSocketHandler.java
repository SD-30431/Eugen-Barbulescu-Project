package com.example.sd_backend2.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class UserWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Principal p = (Principal) session.getAttributes().get("principal");
        String username = p.getName();
        sessions.computeIfAbsent(username, u -> new CopyOnWriteArraySet<>())
                .add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Principal p = (Principal) session.getAttributes().get("principal");
        String username = p.getName();
        CopyOnWriteArraySet<WebSocketSession> userSessions = sessions.get(username);
        if (userSessions != null) {
            userSessions.remove(session);
            if (userSessions.isEmpty()) {
                sessions.remove(username);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (CopyOnWriteArraySet<WebSocketSession> userSet : sessions.values()) {
            for (WebSocketSession sess : userSet) {
                if (sess.isOpen()) {
                     if (sess.getId().equals(session.getId())) continue;
                    sess.sendMessage(message);
                }
            }
        }
    }

//    public boolean sendMessageToUser(String username, String payload) {
//        CopyOnWriteArraySet<WebSocketSession> userSessions = sessions.get(username);
//        if (userSessions == null) {
//            return false;
//        }
//        TextMessage msg = new TextMessage(payload);
//        userSessions.forEach(sess -> {
//            try {
//                if (sess.isOpen()) {
//                    sess.sendMessage(msg);
//                }
//            } catch (Exception e) {
//            }
//        });
//        return true;
//    }
}
