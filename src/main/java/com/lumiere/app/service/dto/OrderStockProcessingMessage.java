package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Kafka message để xử lý giảm stock quantity khi tạo đơn hàng.
 */
public class OrderStockProcessingMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private List<StockDeductionItem> items;

    public OrderStockProcessingMessage() {
    }

    public OrderStockProcessingMessage(Long orderId, List<StockDeductionItem> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<StockDeductionItem> getItems() {
        return items;
    }

    public void setItems(List<StockDeductionItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderStockProcessingMessage{" +
            "orderId=" + orderId +
            ", items=" + items +
            '}';
    }

    /**
     * Thông tin item cần trừ stock.
     */
    public static class StockDeductionItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long variantId;
        private Long quantity;

        public StockDeductionItem() {
        }

        public StockDeductionItem(Long variantId, Long quantity) {
            this.variantId = variantId;
            this.quantity = quantity;
        }

        public Long getVariantId() {
            return variantId;
        }

        public void setVariantId(Long variantId) {
            this.variantId = variantId;
        }

        public Long getQuantity() {
            return quantity;
        }

        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "StockDeductionItem{" +
                "variantId=" + variantId +
                ", quantity=" + quantity +
                '}';
        }
    }
}


