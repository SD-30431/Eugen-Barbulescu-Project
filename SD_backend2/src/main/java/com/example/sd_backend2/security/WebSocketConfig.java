package com.example.sd_backend2.security;

import com.example.sd_backend2.websockets.JwtHandshakeInterceptor;
import com.example.sd_backend2.websockets.UserWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserWebSocketHandler userWebSocketHandler;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    public WebSocketConfig(UserWebSocketHandler handler,
                           JwtTokenUtil jwtTokenUtil,
                           CustomUserDetailsService userDetailsService) {
        this.userWebSocketHandler = handler;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(userWebSocketHandler, "/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenUtil, userDetailsService))
                .setAllowedOriginPatterns("*");
    }
}
