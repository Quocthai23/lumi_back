package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.FlashSaleProduct} entity.
 */
@Schema(description = "Một sản phẩm trong sự kiện Flash Sale.\nFrontend: src/types/flashSale.ts (FlashSaleProductInfo)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashSaleProductDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal salePrice;

    @NotNull
    private Integer quantity;

    @NotNull
    private Integer sold;

    private FlashSaleDTO flashSale;

    private ProductVariantDTO productVariant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public FlashSaleDTO getFlashSale() {
        return flashSale;
    }

    public void setFlashSale(FlashSaleDTO flashSale) {
        this.flashSale = flashSale;
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
        if (!(o instanceof FlashSaleProductDTO)) {
            return false;
        }

        FlashSaleProductDTO flashSaleProductDTO = (FlashSaleProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flashSaleProductDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashSaleProductDTO{" +
            "id=" + getId() +
            ", salePrice=" + getSalePrice() +
            ", quantity=" + getQuantity() +
            ", sold=" + getSold() +
            ", flashSale=" + getFlashSale() +
            ", productVariant=" + getProductVariant() +
            "}";
    }
}
