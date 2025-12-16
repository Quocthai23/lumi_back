package com.lumiere.app.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO cho cart item của khách vãng lai (không cần customerId).
 */
@Getter
@Setter
public class GuestCartItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long variantId;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;

    @NotNull
    private BigDecimal totalPrice;
}

