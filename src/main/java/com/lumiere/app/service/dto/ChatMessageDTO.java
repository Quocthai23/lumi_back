package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.MessageSender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.ChatMessage} entity.
 */
@Schema(description = "Tin nhắn trong phiên chat.\nFrontend: src/types/chat.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatMessageDTO implements Serializable {

    private Long id;

    @NotNull
    private MessageSender sender;

    @Lob
    private String text;

    @NotNull
    private Instant timestamp;

    private ChatSessionDTO session;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageSender getSender() {
        return sender;
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public ChatSessionDTO getSession() {
        return session;
    }

    public void setSession(ChatSessionDTO session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatMessageDTO)) {
            return false;
        }

        ChatMessageDTO chatMessageDTO = (ChatMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chatMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatMessageDTO{" +
            "id=" + getId() +
            ", sender='" + getSender() + "'" +
            ", text='" + getText() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", session=" + getSession() +
            "}";
    }
}
