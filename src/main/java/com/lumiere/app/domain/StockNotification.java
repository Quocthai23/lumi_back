package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Đăng ký nhận thông báo khi có hàng.
 * Frontend: src/types/stockNotification.ts
 */
@Entity
@Table(name = "stock_notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "notified", nullable = false)
    private Boolean notified;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private ProductVariant productVariant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockNotification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public StockNotification email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNotified() {
        return this.notified;
    }

    public StockNotification notified(Boolean notified) {
        this.setNotified(notified);
        return this;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StockNotification createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ProductVariant getProductVariant() {
        return this.productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public StockNotification productVariant(ProductVariant productVariant) {
        this.setProductVariant(productVariant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockNotification)) {
            return false;
        }
        return getId() != null && getId().equals(((StockNotification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockNotification{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", notified='" + getNotified() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
