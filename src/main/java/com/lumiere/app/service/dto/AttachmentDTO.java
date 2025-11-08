package com.lumiere.app.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttachmentDTO implements Serializable {

    private Long id;

    private Long ticketId;

    private Long commentId;

    @NotNull
    @Size(max = 200)
    private String name;

    @NotNull
    @Size(max = 512)
    private String url;

    @Size(max = 128)
    private String contentType;

    @Min(value = 0L)
    private Long size;

    @NotNull
    private Instant uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttachmentDTO)) {
            return false;
        }

        AttachmentDTO attachmentDTO = (AttachmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, attachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttachmentDTO{" +
            "id=" + getId() +
            ", ticketId=" + getTicketId() +
            ", commentId=" + getCommentId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", size=" + getSize() +
            ", uploadedAt='" + getUploadedAt() + "'" +
            "}";
    }
}
