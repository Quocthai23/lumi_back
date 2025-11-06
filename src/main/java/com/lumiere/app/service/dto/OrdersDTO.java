package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.Orders} entity.
 */
@Schema(description = "Đơn hàng của khách hàng.\nFrontend: src/types/order.ts\n@filter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrdersDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private OrderStatus status;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalAmount;

    @Size(max = 500)
    private String note;

    private String paymentMethod;

    @NotNull
    private Instant placedAt;

    @Min(value = 0)
    private Integer redeemedPoints;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Instant placedAt) {
        this.placedAt = placedAt;
    }

    public Integer getRedeemedPoints() {
        return redeemedPoints;
    }

    public void setRedeemedPoints(Integer redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrdersDTO)) {
            return false;
        }

        OrdersDTO ordersDTO = (OrdersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ordersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdersDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", note='" + getNote() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", placedAt='" + getPlacedAt() + "'" +
            ", redeemedPoints=" + getRedeemedPoints() +
            ", customer=" + getCustomer() +
            "}";
    }
}
