package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Sự kiện Flash Sale.
 * Frontend: src/types/flashSale.ts
 */
@Entity
@Table(name = "flash_sale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashSale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flashSale")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "flashSale", "product" }, allowSetters = true)
    private Set<FlashSaleProduct> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlashSale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public FlashSale name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public FlashSale startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public FlashSale endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Set<FlashSaleProduct> getProducts() {
        return this.products;
    }

    public void setProducts(Set<FlashSaleProduct> flashSaleProducts) {
        if (this.products != null) {
            this.products.forEach(i -> i.setFlashSale(null));
        }
        if (flashSaleProducts != null) {
            flashSaleProducts.forEach(i -> i.setFlashSale(this));
        }
        this.products = flashSaleProducts;
    }

    public FlashSale products(Set<FlashSaleProduct> flashSaleProducts) {
        this.setProducts(flashSaleProducts);
        return this;
    }

    public FlashSale addProducts(FlashSaleProduct flashSaleProduct) {
        this.products.add(flashSaleProduct);
        flashSaleProduct.setFlashSale(this);
        return this;
    }

    public FlashSale removeProducts(FlashSaleProduct flashSaleProduct) {
        this.products.remove(flashSaleProduct);
        flashSaleProduct.setFlashSale(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashSale)) {
            return false;
        }
        return getId() != null && getId().equals(((FlashSale) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashSale{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
