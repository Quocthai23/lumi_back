package com.lumiere.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * WebSocket security configuration.
 * Note: For Spring Security 6+, consider using SecurityFilterChain with WebSocketMessageBrokerSecurityConfigurer.
 */
@Configuration
@SuppressWarnings("deprecation")
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Allow system messages (CONNECT, DISCONNECT, CONNECT_ACK, DISCONNECT_ACK, HEARTBEAT, OTHER)
            // These are internal Spring WebSocket messages that don't require authentication
            // This includes error messages and session end messages sent by Spring internally
            .simpTypeMatchers(
                SimpMessageType.CONNECT,
                SimpMessageType.CONNECT_ACK,
                SimpMessageType.DISCONNECT,
                SimpMessageType.DISCONNECT_ACK,
                SimpMessageType.HEARTBEAT,
                SimpMessageType.OTHER
            )
            .permitAll()
            // Allow connection to WebSocket endpoint
            .simpDestMatchers("/ws/**").permitAll()
            // Allow subscribing to public topics without authentication
            .simpSubscribeDestMatchers("/topic/public/**").permitAll()
            // Require authentication for sending messages to application destinations
            .simpDestMatchers("/app/**").authenticated()
            // Require authentication for private messages
            .simpSubscribeDestMatchers("/user/**", "/queue/**").authenticated()
            // Require authentication for session-specific topics
            .simpSubscribeDestMatchers("/topic/session/**").authenticated()
            // For MESSAGE, SUBSCRIBE, UNSUBSCRIBE: require authentication
            // Note: SUBSCRIBE to /topic/public/** is already permitted above with higher priority
            .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE, SimpMessageType.UNSUBSCRIBE)
            .authenticated()
            // Allow any other message type to pass through
            // This handles internal Spring messages (error messages, etc.) that don't match above rules
            // The rules above have already secured all application-specific destinations
            .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Allow cross-origin requests for WebSocket
        // In production, you should configure this more restrictively
        return true;
    }
}

