package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO cho thống kê tổng quan dashboard.
 */
public class DashboardStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal totalRevenue;
    private Long subscriptions; // Khách hàng mới
    private Long sales; // Tổng số đơn hàng
    private Long activeNow; // Hoạt động hiện tại (có thể là số đơn hàng đang xử lý)

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(BigDecimal totalRevenue, Long subscriptions, Long sales, Long activeNow) {
        this.totalRevenue = totalRevenue;
        this.subscriptions = subscriptions;
        this.sales = sales;
        this.activeNow = activeNow;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Long subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public Long getActiveNow() {
        return activeNow;
    }

    public void setActiveNow(Long activeNow) {
        this.activeNow = activeNow;
    }
}

