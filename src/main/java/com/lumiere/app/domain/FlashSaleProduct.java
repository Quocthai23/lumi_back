package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Một sản phẩm trong sự kiện Flash Sale.
 * Frontend: src/types/flashSale.ts (FlashSaleProductInfo)
 */
@Entity
@Table(name = "flash_sale_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashSaleProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "sale_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal salePrice;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "sold", nullable = false)
    private Integer sold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private FlashSale flashSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "flashSaleProducts" }, allowSetters = true)
    private ProductVariant productVariant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlashSaleProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    public FlashSaleProduct salePrice(BigDecimal salePrice) {
        this.setSalePrice(salePrice);
        return this;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public FlashSaleProduct quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSold() {
        return this.sold;
    }

    public FlashSaleProduct sold(Integer sold) {
        this.setSold(sold);
        return this;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public FlashSale getFlashSale() {
        return this.flashSale;
    }

    public void setFlashSale(FlashSale flashSale) {
        this.flashSale = flashSale;
    }

    public FlashSaleProduct flashSale(FlashSale flashSale) {
        this.setFlashSale(flashSale);
        return this;
    }

    public ProductVariant getProductVariant() {
        return this.productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public FlashSaleProduct productVariant(ProductVariant productVariant) {
        this.setProductVariant(productVariant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashSaleProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((FlashSaleProduct) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashSaleProduct{" +
            "id=" + getId() +
            ", salePrice=" + getSalePrice() +
            ", quantity=" + getQuantity() +
            ", sold=" + getSold() +
            "}";
    }
}
