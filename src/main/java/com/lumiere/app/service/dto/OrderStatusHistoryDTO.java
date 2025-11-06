package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.OrderStatusHistory} entity.
 */
@Schema(description = "Lịch sử thay đổi trạng thái đơn hàng.\nFrontend: src/types/order.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderStatusHistoryDTO implements Serializable {

    private Long id;

    @NotNull
    private OrderStatus status;

    private String description;

    @NotNull
    private Instant timestamp;

    private OrdersDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public OrdersDTO getOrder() {
        return order;
    }

    public void setOrder(OrdersDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderStatusHistoryDTO)) {
            return false;
        }

        OrderStatusHistoryDTO orderStatusHistoryDTO = (OrderStatusHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderStatusHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderStatusHistoryDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
