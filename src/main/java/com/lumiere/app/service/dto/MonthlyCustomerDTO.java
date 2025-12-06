package com.lumiere.app.service.dto;

import java.io.Serializable;

/**
 * DTO cho số lượng khách hàng mới theo tháng.
 */
public class MonthlyCustomerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String month; // Format: "Thg 1", "Thg 2", ...
    private Long count;

    public MonthlyCustomerDTO() {}

    public MonthlyCustomerDTO(String month, Long count) {
        this.month = month;
        this.count = count;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

