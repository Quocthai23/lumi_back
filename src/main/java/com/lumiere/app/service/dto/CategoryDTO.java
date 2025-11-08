package com.lumiere.app.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class CategoryDTO implements Serializable {

    private Long id;

    @NotBlank
    @Size(max = 200)
    private String name;

    private Long fatherId;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getFatherId() { return fatherId; }
    public void setFatherId(Long fatherId) { this.fatherId = fatherId; }

    @Override
    public String toString() {
        return "CategoryDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", fatherId=" + fatherId +
            '}';
    }
}
