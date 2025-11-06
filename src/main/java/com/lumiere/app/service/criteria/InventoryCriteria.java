package com.lumiere.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumiere.app.domain.Inventory} entity. This class is used
 * in {@link com.lumiere.app.web.rest.InventoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /inventories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter stockQuantity;

    private LongFilter productVariantId;

    private LongFilter warehouseId;

    private Boolean distinct;

    public InventoryCriteria() {}

    public InventoryCriteria(InventoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.stockQuantity = other.optionalStockQuantity().map(LongFilter::copy).orElse(null);
        this.productVariantId = other.optionalProductVariantId().map(LongFilter::copy).orElse(null);
        this.warehouseId = other.optionalWarehouseId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InventoryCriteria copy() {
        return new InventoryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getStockQuantity() {
        return stockQuantity;
    }

    public Optional<LongFilter> optionalStockQuantity() {
        return Optional.ofNullable(stockQuantity);
    }

    public LongFilter stockQuantity() {
        if (stockQuantity == null) {
            setStockQuantity(new LongFilter());
        }
        return stockQuantity;
    }

    public void setStockQuantity(LongFilter stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LongFilter getProductVariantId() {
        return productVariantId;
    }

    public Optional<LongFilter> optionalProductVariantId() {
        return Optional.ofNullable(productVariantId);
    }

    public LongFilter productVariantId() {
        if (productVariantId == null) {
            setProductVariantId(new LongFilter());
        }
        return productVariantId;
    }

    public void setProductVariantId(LongFilter productVariantId) {
        this.productVariantId = productVariantId;
    }

    public LongFilter getWarehouseId() {
        return warehouseId;
    }

    public Optional<LongFilter> optionalWarehouseId() {
        return Optional.ofNullable(warehouseId);
    }

    public LongFilter warehouseId() {
        if (warehouseId == null) {
            setWarehouseId(new LongFilter());
        }
        return warehouseId;
    }

    public void setWarehouseId(LongFilter warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InventoryCriteria that = (InventoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(stockQuantity, that.stockQuantity) &&
            Objects.equals(productVariantId, that.productVariantId) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stockQuantity, productVariantId, warehouseId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStockQuantity().map(f -> "stockQuantity=" + f + ", ").orElse("") +
            optionalProductVariantId().map(f -> "productVariantId=" + f + ", ").orElse("") +
            optionalWarehouseId().map(f -> "warehouseId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
