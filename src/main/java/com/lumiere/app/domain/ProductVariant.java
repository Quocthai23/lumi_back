package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Biến thể của sản phẩm (ví dụ: size, màu sắc).
 * Frontend: src/types/product.ts
 * @filter
 */
@Entity
@Table(name = "product_variant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVariant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    @Column(name = "sku", length = 64, nullable = false, unique = true)
    private String sku;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @DecimalMin(value = "0")
    @Column(name = "compare_at_price", precision = 21, scale = 2)
    private BigDecimal compareAtPrice;

    @Size(max = 3)
    @Column(name = "currency", length = 3)
    private String currency;

    @NotNull
    @Min(value = 0L)
    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @NotNull
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "variants", "reviews", "questions", "collections", "wishlistedBies" }, allowSetters = true)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductVariant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return this.sku;
    }

    public ProductVariant sku(String sku) {
        this.setSku(sku);
        return this;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return this.name;
    }

    public ProductVariant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public ProductVariant price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCompareAtPrice() {
        return this.compareAtPrice;
    }

    public ProductVariant compareAtPrice(BigDecimal compareAtPrice) {
        this.setCompareAtPrice(compareAtPrice);
        return this;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public String getCurrency() {
        return this.currency;
    }

    public ProductVariant currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getStockQuantity() {
        return this.stockQuantity;
    }

    public ProductVariant stockQuantity(Long stockQuantity) {
        this.setStockQuantity(stockQuantity);
        return this;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public ProductVariant isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getColor() {
        return this.color;
    }

    public ProductVariant color(String color) {
        this.setColor(color);
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return this.size;
    }

    public ProductVariant size(String size) {
        this.setSize(size);
        return this;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant product(Product product) {
        this.setProduct(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariant)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductVariant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVariant{" +
            "id=" + getId() +
            ", sku='" + getSku() + "'" +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", compareAtPrice=" + getCompareAtPrice() +
            ", currency='" + getCurrency() + "'" +
            ", stockQuantity=" + getStockQuantity() +
            ", isDefault='" + getIsDefault() + "'" +
            ", color='" + getColor() + "'" +
            ", size='" + getSize() + "'" +
            "}";
    }
}
