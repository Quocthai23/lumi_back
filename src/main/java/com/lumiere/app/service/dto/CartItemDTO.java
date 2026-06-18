package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO implements Serializable {

    private Long id;

    private Long customerId;

    private Long productId;

    private Long variantId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private Instant createdDate;

    private Instant lastModifiedDate;

    private Long variantStock;

    private ProductVariantDTO variant;
}
