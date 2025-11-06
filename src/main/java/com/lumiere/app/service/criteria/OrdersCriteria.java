package com.lumiere.app.service.criteria;

import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.PaymentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumiere.app.domain.Orders} entity. This class is used
 * in {@link com.lumiere.app.web.rest.OrdersResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrdersCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    /**
     * Class for filtering PaymentStatus
     */
    public static class PaymentStatusFilter extends Filter<PaymentStatus> {

        public PaymentStatusFilter() {}

        public PaymentStatusFilter(PaymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public PaymentStatusFilter copy() {
            return new PaymentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private OrderStatusFilter status;

    private PaymentStatusFilter paymentStatus;

    private BigDecimalFilter totalAmount;

    private StringFilter note;

    private StringFilter paymentMethod;

    private InstantFilter placedAt;

    private IntegerFilter redeemedPoints;

    private LongFilter customerId;

    private LongFilter orderItemsId;

    private LongFilter orderStatusHistoryId;

    private Boolean distinct;

    public OrdersCriteria() {}

    public OrdersCriteria(OrdersCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(OrderStatusFilter::copy).orElse(null);
        this.paymentStatus = other.optionalPaymentStatus().map(PaymentStatusFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.paymentMethod = other.optionalPaymentMethod().map(StringFilter::copy).orElse(null);
        this.placedAt = other.optionalPlacedAt().map(InstantFilter::copy).orElse(null);
        this.redeemedPoints = other.optionalRedeemedPoints().map(IntegerFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.orderItemsId = other.optionalOrderItemsId().map(LongFilter::copy).orElse(null);
        this.orderStatusHistoryId = other.optionalOrderStatusHistoryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrdersCriteria copy() {
        return new OrdersCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public OrderStatusFilter getStatus() {
        return status;
    }

    public Optional<OrderStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public OrderStatusFilter status() {
        if (status == null) {
            setStatus(new OrderStatusFilter());
        }
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
        this.status = status;
    }

    public PaymentStatusFilter getPaymentStatus() {
        return paymentStatus;
    }

    public Optional<PaymentStatusFilter> optionalPaymentStatus() {
        return Optional.ofNullable(paymentStatus);
    }

    public PaymentStatusFilter paymentStatus() {
        if (paymentStatus == null) {
            setPaymentStatus(new PaymentStatusFilter());
        }
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusFilter paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public StringFilter getPaymentMethod() {
        return paymentMethod;
    }

    public Optional<StringFilter> optionalPaymentMethod() {
        return Optional.ofNullable(paymentMethod);
    }

    public StringFilter paymentMethod() {
        if (paymentMethod == null) {
            setPaymentMethod(new StringFilter());
        }
        return paymentMethod;
    }

    public void setPaymentMethod(StringFilter paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public InstantFilter getPlacedAt() {
        return placedAt;
    }

    public Optional<InstantFilter> optionalPlacedAt() {
        return Optional.ofNullable(placedAt);
    }

    public InstantFilter placedAt() {
        if (placedAt == null) {
            setPlacedAt(new InstantFilter());
        }
        return placedAt;
    }

    public void setPlacedAt(InstantFilter placedAt) {
        this.placedAt = placedAt;
    }

    public IntegerFilter getRedeemedPoints() {
        return redeemedPoints;
    }

    public Optional<IntegerFilter> optionalRedeemedPoints() {
        return Optional.ofNullable(redeemedPoints);
    }

    public IntegerFilter redeemedPoints() {
        if (redeemedPoints == null) {
            setRedeemedPoints(new IntegerFilter());
        }
        return redeemedPoints;
    }

    public void setRedeemedPoints(IntegerFilter redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getOrderItemsId() {
        return orderItemsId;
    }

    public Optional<LongFilter> optionalOrderItemsId() {
        return Optional.ofNullable(orderItemsId);
    }

    public LongFilter orderItemsId() {
        if (orderItemsId == null) {
            setOrderItemsId(new LongFilter());
        }
        return orderItemsId;
    }

    public void setOrderItemsId(LongFilter orderItemsId) {
        this.orderItemsId = orderItemsId;
    }

    public LongFilter getOrderStatusHistoryId() {
        return orderStatusHistoryId;
    }

    public Optional<LongFilter> optionalOrderStatusHistoryId() {
        return Optional.ofNullable(orderStatusHistoryId);
    }

    public LongFilter orderStatusHistoryId() {
        if (orderStatusHistoryId == null) {
            setOrderStatusHistoryId(new LongFilter());
        }
        return orderStatusHistoryId;
    }

    public void setOrderStatusHistoryId(LongFilter orderStatusHistoryId) {
        this.orderStatusHistoryId = orderStatusHistoryId;
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
        final OrdersCriteria that = (OrdersCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(status, that.status) &&
            Objects.equals(paymentStatus, that.paymentStatus) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(note, that.note) &&
            Objects.equals(paymentMethod, that.paymentMethod) &&
            Objects.equals(placedAt, that.placedAt) &&
            Objects.equals(redeemedPoints, that.redeemedPoints) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(orderItemsId, that.orderItemsId) &&
            Objects.equals(orderStatusHistoryId, that.orderStatusHistoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            status,
            paymentStatus,
            totalAmount,
            note,
            paymentMethod,
            placedAt,
            redeemedPoints,
            customerId,
            orderItemsId,
            orderStatusHistoryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdersCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPaymentStatus().map(f -> "paymentStatus=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalPaymentMethod().map(f -> "paymentMethod=" + f + ", ").orElse("") +
            optionalPlacedAt().map(f -> "placedAt=" + f + ", ").orElse("") +
            optionalRedeemedPoints().map(f -> "redeemedPoints=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalOrderItemsId().map(f -> "orderItemsId=" + f + ", ").orElse("") +
            optionalOrderStatusHistoryId().map(f -> "orderStatusHistoryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
