package com.lumiere.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time chat.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry messages back to the client
        // on destinations prefixed with "/topic" and "/queue"
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for messages FROM client TO server
        config.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint, enabling SockJS fallback options
        // SockJS allows fallback options for browsers that don't support WebSocket
        // The /ws/info endpoint is used by SockJS for handshake and requires CORS
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .setAllowedOrigins("http://localhost:5173")
            .withSockJS()
            .setSessionCookieNeeded(false); // Disable session cookie for better CORS support

        // Also register without SockJS for native WebSocket clients
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:5173");
    }
}

