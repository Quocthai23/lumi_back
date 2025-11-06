package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Phiên chat.
 * Frontend: src/types/chat.ts
 */
@Entity
@Table(name = "chat_session")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "session" }, allowSetters = true)
    private Set<ChatMessage> messages = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChatSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public ChatSession customerId(String customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ChatSession createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<ChatMessage> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<ChatMessage> chatMessages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setSession(null));
        }
        if (chatMessages != null) {
            chatMessages.forEach(i -> i.setSession(this));
        }
        this.messages = chatMessages;
    }

    public ChatSession messages(Set<ChatMessage> chatMessages) {
        this.setMessages(chatMessages);
        return this;
    }

    public ChatSession addMessages(ChatMessage chatMessage) {
        this.messages.add(chatMessage);
        chatMessage.setSession(this);
        return this;
    }

    public ChatSession removeMessages(ChatMessage chatMessage) {
        this.messages.remove(chatMessage);
        chatMessage.setSession(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatSession)) {
            return false;
        }
        return getId() != null && getId().equals(((ChatSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatSession{" +
            "id=" + getId() +
            ", customerId='" + getCustomerId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
