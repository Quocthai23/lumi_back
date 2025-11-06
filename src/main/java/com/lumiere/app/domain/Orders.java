package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Đơn hàng của khách hàng.
 * Frontend: src/types/order.ts
 * @filter
 */
@Entity
@Table(name = "orders")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "payment_method")
    private String paymentMethod;

    @NotNull
    @Column(name = "placed_at", nullable = false)
    private Instant placedAt;

    @Min(value = 0)
    @Column(name = "redeemed_points")
    private Integer redeemedPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "orders", "wishlists", "addresses", "loyaltyHistories", "notifications" }, allowSetters = true)
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "order", "productVariant" }, allowSetters = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private Set<OrderStatusHistory> orderStatusHistories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Orders id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Orders code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Orders status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public Orders paymentStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Orders totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return this.note;
    }

    public Orders note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public Orders paymentMethod(String paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getPlacedAt() {
        return this.placedAt;
    }

    public Orders placedAt(Instant placedAt) {
        this.setPlacedAt(placedAt);
        return this;
    }

    public void setPlacedAt(Instant placedAt) {
        this.placedAt = placedAt;
    }

    public Integer getRedeemedPoints() {
        return this.redeemedPoints;
    }

    public Orders redeemedPoints(Integer redeemedPoints) {
        this.setRedeemedPoints(redeemedPoints);
        return this;
    }

    public void setRedeemedPoints(Integer redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Orders customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Set<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        if (this.orderItems != null) {
            this.orderItems.forEach(i -> i.setOrder(null));
        }
        if (orderItems != null) {
            orderItems.forEach(i -> i.setOrder(this));
        }
        this.orderItems = orderItems;
    }

    public Orders orderItems(Set<OrderItem> orderItems) {
        this.setOrderItems(orderItems);
        return this;
    }

    public Orders addOrderItems(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
        return this;
    }

    public Orders removeOrderItems(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public Set<OrderStatusHistory> getOrderStatusHistories() {
        return this.orderStatusHistories;
    }

    public void setOrderStatusHistories(Set<OrderStatusHistory> orderStatusHistories) {
        if (this.orderStatusHistories != null) {
            this.orderStatusHistories.forEach(i -> i.setOrder(null));
        }
        if (orderStatusHistories != null) {
            orderStatusHistories.forEach(i -> i.setOrder(this));
        }
        this.orderStatusHistories = orderStatusHistories;
    }

    public Orders orderStatusHistories(Set<OrderStatusHistory> orderStatusHistories) {
        this.setOrderStatusHistories(orderStatusHistories);
        return this;
    }

    public Orders addOrderStatusHistory(OrderStatusHistory orderStatusHistory) {
        this.orderStatusHistories.add(orderStatusHistory);
        orderStatusHistory.setOrder(this);
        return this;
    }

    public Orders removeOrderStatusHistory(OrderStatusHistory orderStatusHistory) {
        this.orderStatusHistories.remove(orderStatusHistory);
        orderStatusHistory.setOrder(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Orders)) {
            return false;
        }
        return getId() != null && getId().equals(((Orders) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Orders{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", note='" + getNote() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", placedAt='" + getPlacedAt() + "'" +
            ", redeemedPoints=" + getRedeemedPoints() +
            "}";
    }
}
