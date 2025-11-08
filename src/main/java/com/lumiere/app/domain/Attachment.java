package com.lumiere.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Attachment.
 */
@Entity
@Table(name = "attachment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "comment_id")
    private Long commentId;

    @NotNull
    @Size(max = 200)
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @NotNull
    @Size(max = 512)
    @Column(name = "url", length = 512, nullable = false)
    private String url;

    @Size(max = 128)
    @Column(name = "content_type", length = 128)
    private String contentType;

    @Min(value = 0L)
    @Column(name = "size")
    private Long size;

    @NotNull
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Attachment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return this.ticketId;
    }

    public Attachment ticketId(Long ticketId) {
        this.setTicketId(ticketId);
        return this;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getCommentId() {
        return this.commentId;
    }

    public Attachment commentId(Long commentId) {
        this.setCommentId(commentId);
        return this;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getName() {
        return this.name;
    }

    public Attachment name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public Attachment url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Attachment contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return this.size;
    }

    public Attachment size(Long size) {
        this.setSize(size);
        return this;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public Attachment uploadedAt(Instant uploadedAt) {
        this.setUploadedAt(uploadedAt);
        return this;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attachment)) {
            return false;
        }
        return getId() != null && getId().equals(((Attachment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attachment{" +
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
