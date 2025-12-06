package com.lumiere.app.web.rest;

import com.lumiere.app.domain.ChatMessage;
import com.lumiere.app.domain.ChatSession;
import com.lumiere.app.domain.enumeration.MessageSender;
import com.lumiere.app.repository.ChatMessageRepository;
import com.lumiere.app.repository.ChatSessionRepository;
import com.lumiere.app.security.SecurityUtils;
import com.lumiere.app.service.dto.ChatMessageDTO;
import com.lumiere.app.service.mapper.ChatMessageMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

/**
 * WebSocket controller for handling real-time chat messages.
 */
@Controller
public class WebSocketController {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageMapper chatMessageMapper;

    public WebSocketController(
        SimpMessagingTemplate messagingTemplate,
        ChatMessageRepository chatMessageRepository,
        ChatSessionRepository chatSessionRepository,
        ChatMessageMapper chatMessageMapper
    ) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageMapper = chatMessageMapper;
    }

    /**
     * Handle incoming chat messages.
     * Client sends to: /app/chat.sendMessage
     * Server broadcasts to: /topic/public (for all users) or /topic/session/{sessionId} (for specific session)
     *
     * @param messageDTO the message DTO
     * @return the saved message DTO
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    @Transactional
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO messageDTO) {
        LOG.debug("Received message: {}", messageDTO);

        // Get or create chat session
        ChatSession session = getOrCreateSession(messageDTO);

        // Create and save message
        ChatMessage message = new ChatMessage();
        message.setSender(messageDTO.getSender() != null ? messageDTO.getSender() : MessageSender.USER);
        message.setText(messageDTO.getText());
        message.setTimestamp(messageDTO.getTimestamp() != null ? messageDTO.getTimestamp() : Instant.now());
        message.setSession(session);

        message = chatMessageRepository.save(message);
        ChatMessageDTO savedMessage = chatMessageMapper.toDto(message);

        LOG.info("Saved and broadcasting message: {}", savedMessage.getId());

        // Broadcast to specific session topic
        messagingTemplate.convertAndSend("/topic/session/" + session.getId(), savedMessage);

        return savedMessage;
    }

    /**
     * Handle private messages (user to user).
     * Client sends to: /app/chat.sendPrivateMessage
     * Server sends to: /user/{username}/queue/private
     *
     * @param messageDTO the message DTO
     */
    @MessageMapping("/chat.sendPrivateMessage")
    @SendToUser("/queue/private")
    @Transactional
    public ChatMessageDTO sendPrivateMessage(@Payload ChatMessageDTO messageDTO) {
        LOG.debug("Received private message: {}", messageDTO);

        // Get or create chat session
        ChatSession session = getOrCreateSession(messageDTO);

        // Create and save message
        ChatMessage message = new ChatMessage();
        message.setSender(messageDTO.getSender() != null ? messageDTO.getSender() : MessageSender.USER);
        message.setText(messageDTO.getText());
        message.setTimestamp(messageDTO.getTimestamp() != null ? messageDTO.getTimestamp() : Instant.now());
        message.setSession(session);

        message = chatMessageRepository.save(message);
        ChatMessageDTO savedMessage = chatMessageMapper.toDto(message);

        LOG.info("Saved private message: {}", savedMessage.getId());

        return savedMessage;
    }

    /**
     * Handle user joining a chat session.
     * Client sends to: /app/chat.addUser
     * Server broadcasts to: /topic/public
     *
     * @param messageDTO the message DTO containing session info
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    @Transactional
    public ChatMessageDTO addUser(@Payload ChatMessageDTO messageDTO) {
        LOG.debug("User joining chat: {}", messageDTO);

        // Get or create chat session
        ChatSession session = getOrCreateSession(messageDTO);

        // Create join notification message
        ChatMessage message = new ChatMessage();
        message.setSender(MessageSender.BOT);
        message.setText("User joined the chat");
        message.setTimestamp(Instant.now());
        message.setSession(session);

        message = chatMessageRepository.save(message);
        ChatMessageDTO savedMessage = chatMessageMapper.toDto(message);

        // Broadcast to session
        messagingTemplate.convertAndSend("/topic/session/" + session.getId(), savedMessage);

        return savedMessage;
    }

    /**
     * Get or create a chat session.
     *
     * @param messageDTO the message DTO
     * @return the chat session
     */
    private ChatSession getOrCreateSession(ChatMessageDTO messageDTO) {
        Long sessionId = null;
        if (messageDTO.getSession() != null && messageDTO.getSession().getId() != null) {
            sessionId = messageDTO.getSession().getId();
        }

        ChatSession session;
        if (sessionId != null) {
            Optional<ChatSession> existingSession = chatSessionRepository.findById(sessionId);
            if (existingSession.isPresent()) {
                session = existingSession.get();
            } else {
                session = createNewSession(messageDTO);
            }
        } else {
            // Try to find session by customerId
            String customerId = getCurrentCustomerId();
            if (customerId != null) {
                Optional<ChatSession> existingSession = chatSessionRepository
                    .findAll()
                    .stream()
                    .filter(s -> customerId.equals(s.getCustomerId()))
                    .findFirst();
                if (existingSession.isPresent()) {
                    session = existingSession.get();
                } else {
                    session = createNewSession(messageDTO);
                }
            } else {
                session = createNewSession(messageDTO);
            }
        }

        return session;
    }

    /**
     * Create a new chat session.
     *
     * @param messageDTO the message DTO
     * @return the new chat session
     */
    private ChatSession createNewSession(ChatMessageDTO messageDTO) {
        ChatSession session = new ChatSession();
        String customerId = getCurrentCustomerId();
        if (customerId == null && messageDTO.getSession() != null) {
            customerId = messageDTO.getSession().getCustomerId();
        }
        session.setCustomerId(customerId);
        session.setCreatedAt(Instant.now());
        session = chatSessionRepository.save(session);
        LOG.debug("Created new chat session: {}", session.getId());
        return session;
    }

    /**
     * Get current customer ID from security context.
     *
     * @return customer ID or null
     */
    private String getCurrentCustomerId() {
        Optional<Long> userIdOpt = SecurityUtils.getCurrentUserId();
        return userIdOpt.map(String::valueOf).orElse(null);
    }
}

