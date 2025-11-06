package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.ChatSession} entity.
 */
@Schema(description = "Phiên chat.\nFrontend: src/types/chat.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatSessionDTO implements Serializable {

    private Long id;

    private String customerId;

    @NotNull
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatSessionDTO)) {
            return false;
        }

        ChatSessionDTO chatSessionDTO = (ChatSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chatSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatSessionDTO{" +
            "id=" + getId() +
            ", customerId='" + getCustomerId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
