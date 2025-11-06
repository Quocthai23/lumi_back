package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.Warehouse} entity.
 */
@Schema(description = "Kho hàng.\nFrontend: src/types/warehouse.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String address;

    @NotNull
    private Boolean isActive;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseDTO)) {
            return false;
        }

        WarehouseDTO warehouseDTO = (WarehouseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, warehouseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
