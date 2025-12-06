package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Voucher đã được tặng cho khách hàng.
 */
@Entity
@Table(name = "customer_voucher")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomerVoucher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "orders", "wishlists", "addresses", "loyaltyHistories", "notifications" }, allowSetters = true)
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {}, allowSetters = true)
    private Voucher voucher;

    @NotNull
    @Column(name = "gifted_at", nullable = false)
    private Instant giftedAt;

    @Column(name = "quarter")
    private String quarter; // Format: "2024-Q1", "2024-Q2", etc.

    @Column(name = "used")
    private Boolean used = false;

    public CustomerVoucher() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerVoucher customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public CustomerVoucher voucher(Voucher voucher) {
        this.setVoucher(voucher);
        return this;
    }

    public Instant getGiftedAt() {
        return giftedAt;
    }

    public void setGiftedAt(Instant giftedAt) {
        this.giftedAt = giftedAt;
    }

    public CustomerVoucher giftedAt(Instant giftedAt) {
        this.setGiftedAt(giftedAt);
        return this;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public CustomerVoucher quarter(String quarter) {
        this.setQuarter(quarter);
        return this;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public CustomerVoucher used(Boolean used) {
        this.setUsed(used);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerVoucher)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerVoucher) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CustomerVoucher{" +
            "id=" + getId() +
            ", giftedAt='" + getGiftedAt() + "'" +
            ", quarter='" + getQuarter() + "'" +
            ", used=" + getUsed() +
            "}";
    }
}

