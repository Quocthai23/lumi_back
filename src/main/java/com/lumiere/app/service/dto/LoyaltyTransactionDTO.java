package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.LoyaltyTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.LoyaltyTransaction} entity.
 */
@Schema(description = "Lịch sử điểm thưởng của khách hàng.\nFrontend: src/pages/customer/LoyaltyPage.tsx")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoyaltyTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private LoyaltyTransactionType type;

    @NotNull
    private Integer points;

    private String description;

    @NotNull
    private Instant createdAt;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoyaltyTransactionType getType() {
        return type;
    }

    public void setType(LoyaltyTransactionType type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoyaltyTransactionDTO)) {
            return false;
        }

        LoyaltyTransactionDTO loyaltyTransactionDTO = (LoyaltyTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, loyaltyTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoyaltyTransactionDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", points=" + getPoints() +
            ", description='" + getDescription() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
