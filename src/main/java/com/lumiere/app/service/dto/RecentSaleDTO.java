package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO cho đơn hàng gần đây.
 */
public class RecentSaleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name; // Tên khách hàng
    private String email; // Email khách hàng
    private BigDecimal amount; // Số tiền đơn hàng

    public RecentSaleDTO() {}

    public RecentSaleDTO(String name, String email, BigDecimal amount) {
        this.name = name;
        this.email = email;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

