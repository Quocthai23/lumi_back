package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO cho doanh thu theo tháng.
 */
public class MonthlyRevenueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String month; // Format: "Thg 1", "Thg 2", ...
    private BigDecimal total;

    public MonthlyRevenueDTO() {}

    public MonthlyRevenueDTO(String month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

