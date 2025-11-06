package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumiere.app.domain.enumeration.StockMovementReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Lịch sử thay đổi tồn kho (để kiểm toán).
 */
@Entity
@Table(name = "stock_movement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity_change", nullable = false)
    private Long quantityChange;

    @Column(name = "note")
    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private StockMovementReason reason;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse warehouse;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockMovement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuantityChange() {
        return this.quantityChange;
    }

    public StockMovement quantityChange(Long quantityChange) {
        this.setQuantityChange(quantityChange);
        return this;
    }

    public void setQuantityChange(Long quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getNote() {
        return this.note;
    }

    public StockMovement note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StockMovementReason getReason() {
        return this.reason;
    }

    public StockMovement reason(StockMovementReason reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(StockMovementReason reason) {
        this.reason = reason;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StockMovement createdAt(Instant createdAt) {
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

    public StockMovement productVariant(ProductVariant productVariant) {
        this.setProductVariant(productVariant);
        return this;
    }

    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public StockMovement warehouse(Warehouse warehouse) {
        this.setWarehouse(warehouse);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMovement)) {
            return false;
        }
        return getId() != null && getId().equals(((StockMovement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovement{" +
            "id=" + getId() +
            ", quantityChange=" + getQuantityChange() +
            ", note='" + getNote() + "'" +
            ", reason='" + getReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
