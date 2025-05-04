package com.example.sd_backend2.websockets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class EchoHandler extends TextWebSocketHandler {

    // track sessions if you ever want to broadcast
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // echo back to the same client
        session.sendMessage(new TextMessage("Server got: " + message.getPayload()));
    }

    // optional helper to broadcast to all connected clients
    public void broadcast(String msg) throws Exception {
        TextMessage tm = new TextMessage(msg);
        for (WebSocketSession sess : sessions) {
            if (sess.isOpen()) {
                sess.sendMessage(tm);
            }
        }
    }
}
