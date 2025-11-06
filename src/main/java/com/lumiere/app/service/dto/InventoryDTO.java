package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.Inventory} entity.
 */
@Schema(description = "Tồn kho chi tiết: số lượng của 1 biến thể tại 1 kho.\nFrontend: src/types/inventory.ts\n@filter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0L)
    private Long stockQuantity;

    private ProductVariantDTO productVariant;

    private WarehouseDTO warehouse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
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
        if (!(o instanceof InventoryDTO)) {
            return false;
        }

        InventoryDTO inventoryDTO = (InventoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inventoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryDTO{" +
            "id=" + getId() +
            ", stockQuantity=" + getStockQuantity() +
            ", productVariant=" + getProductVariant() +
            ", warehouse=" + getWarehouse() +
            "}";
    }
}
