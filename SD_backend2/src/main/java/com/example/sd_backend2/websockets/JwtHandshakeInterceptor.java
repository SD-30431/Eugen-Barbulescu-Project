package com.example.sd_backend2.websockets;

import com.example.sd_backend2.security.JwtTokenUtil;
import com.example.sd_backend2.security.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtHandshakeInterceptor(JwtTokenUtil jwtTokenUtil,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String query = request.getURI().getQuery();
        if (query == null || !query.startsWith("token=")) {
            return false;
        }

        String rawToken = query.substring("token=".length());
        String token = URLDecoder.decode(rawToken, StandardCharsets.UTF_8.name());

        String username;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (!jwtTokenUtil.validateToken(token, user)) {
            return false;
        }

        Principal principal =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        attributes.put("principal", principal);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }
}
