package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.StockNotification} entity.
 */
@Schema(description = "Đăng ký nhận thông báo khi có hàng.\nFrontend: src/types/stockNotification.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockNotificationDTO implements Serializable {

    private Long id;

    @NotNull
    private String email;

    @NotNull
    private Boolean notified;

    @NotNull
    private Instant createdAt;

    private ProductVariantDTO productVariant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ProductVariantDTO getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariantDTO productVariant) {
        this.productVariant = productVariant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockNotificationDTO)) {
            return false;
        }

        StockNotificationDTO stockNotificationDTO = (StockNotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockNotificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockNotificationDTO{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", notified='" + getNotified() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", productVariant=" + getProductVariant() +
            "}";
    }
}
