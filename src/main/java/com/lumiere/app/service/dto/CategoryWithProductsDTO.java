package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho category kèm danh sách sản phẩm mẫu.
 */
public class CategoryWithProductsDTO implements Serializable {

    private Long id;
    private String name;
    private Long fatherId;
    private List<ProductDTO> products = new ArrayList<>();

    public CategoryWithProductsDTO() {}

    public CategoryWithProductsDTO(Long id, String name, Long fatherId) {
        this.id = id;
        this.name = name;
        this.fatherId = fatherId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}

