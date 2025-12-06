package com.lumiere.app.service.dto;

import java.io.Serializable;

/**
 * DTO cho top sản phẩm bán chạy.
 */
public class TopProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long total; // Tổng số lượng bán được

    public TopProductDTO() {}

    public TopProductDTO(String name, Long total) {
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}

