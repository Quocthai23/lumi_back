package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.StockMovementReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.StockMovement} entity.
 */
@Schema(description = "Lịch sử thay đổi tồn kho (để kiểm toán).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovementDTO implements Serializable {

    private Long id;

    @NotNull
    private Long quantityChange;

    private String note;

    @NotNull
    private StockMovementReason reason;

    @NotNull
    private Instant createdAt;

    private ProductVariantDTO productVariant;

    private WarehouseDTO warehouse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Long quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StockMovementReason getReason() {
        return reason;
    }

    public void setReason(StockMovementReason reason) {
        this.reason = reason;
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

    public WarehouseDTO getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseDTO warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMovementDTO)) {
            return false;
        }

        StockMovementDTO stockMovementDTO = (StockMovementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockMovementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovementDTO{" +
            "id=" + getId() +
            ", quantityChange=" + getQuantityChange() +
            ", note='" + getNote() + "'" +
            ", reason='" + getReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", productVariant=" + getProductVariant() +
            ", warehouse=" + getWarehouse() +
            "}";
    }
}
