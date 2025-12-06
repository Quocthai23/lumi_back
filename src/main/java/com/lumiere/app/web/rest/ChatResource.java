package com.lumiere.app.web.rest;

import com.lumiere.app.service.ChatMessageService;
import com.lumiere.app.service.dto.ChatMessageDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for chat operations.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChatResource.class);

    private final ChatMessageService chatMessageService;

    public ChatResource(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * {@code POST  /chat/messages} : Create a new chat message for a contact message.
     *
     * @param request the request containing message, contactMessageId, and sender.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chatMessageDTO.
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> createMessage(@Valid @RequestBody ChatMessageRequest request) {
        LOG.debug("REST request to save ChatMessage for ContactMessage : {}", request);
        ChatMessageDTO chatMessageDTO = chatMessageService.saveMessageForContact(
            request.getMessage(),
            request.getContactMessageId(),
            request.getSender()
        );
        return ResponseEntity.ok().body(chatMessageDTO);
    }

    /**
     * {@code GET  /chat/messages/:contactMessageId} : Get all messages for a contact message.
     *
     * @param contactMessageId the id of the contactMessage.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of messages in body.
     */
    @GetMapping("/messages/{contactMessageId}")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesByContactMessageId(
        @PathVariable("contactMessageId") Long contactMessageId
    ) {
        LOG.debug("REST request to get all ChatMessages for ContactMessage : {}", contactMessageId);
        List<ChatMessageDTO> messages = chatMessageService.findByContactMessageId(contactMessageId);
        return ResponseEntity.ok().body(messages);
    }

    /**
     * Request DTO for creating a chat message.
     */
    public static class ChatMessageRequest {

        @NotNull
        private String message;

        @NotNull
        private Long contactMessageId;

        @NotNull
        private String sender;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getContactMessageId() {
            return contactMessageId;
        }

        public void setContactMessageId(Long contactMessageId) {
            this.contactMessageId = contactMessageId;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }
    }
}

