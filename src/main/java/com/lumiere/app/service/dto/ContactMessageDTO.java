package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.ContactStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.ContactMessage} entity.
 */
@Schema(description = "Tin nhắn liên hệ từ form liên hệ.\nFrontend: src/types/contact.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContactMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String fullName;

    @NotNull
    @Email
    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    private String subject;

    @NotNull
    @Lob
    private String message;

    private ContactStatus status;

    private String adminNote;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContactStatus getStatus() {
        return status;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
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
        if (!(o instanceof ContactMessageDTO)) {
            return false;
        }

        ContactMessageDTO contactMessageDTO = (ContactMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contactMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactMessageDTO{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", email='" + getEmail() + "'" +
            ", subject='" + getSubject() + "'" +
            ", message='" + getMessage() + "'" +
            ", status='" + getStatus() + "'" +
            ", adminNote='" + getAdminNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}

