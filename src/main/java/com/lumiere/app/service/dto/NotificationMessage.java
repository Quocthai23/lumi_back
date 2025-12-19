package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.time.Instant;

/**
 * Kafka message DTO cho notification.
 */
public class NotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotificationType type;
    private String message;
    private String link;
    private Long customerId; // null nếu là notification cho admin
    private Instant createdAt;

    public NotificationMessage() {
        this.createdAt = Instant.now();
    }

    public NotificationMessage(NotificationType type, String message, String link, Long customerId) {
        this.type = type;
        this.message = message;
        this.link = link;
        this.customerId = customerId;
        this.createdAt = Instant.now();
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
            "type=" + type +
            ", message='" + message + '\'' +
            ", link='" + link + '\'' +
            ", customerId=" + customerId +
            ", createdAt=" + createdAt +
            '}';
    }
}















