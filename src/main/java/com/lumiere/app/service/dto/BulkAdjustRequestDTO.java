// src/main/java/com/lumi/app/service/dto/BulkAdjustRequestDTO.java
package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.AdjustmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BulkAdjustRequestDTO {

    @NotNull
    private AdjustmentType type;

    /** Tham chiếu nghiệp vụ: ORDER/GRN/RETURN/... */
    private String refType;
    private String refCode;

    /** Ai thực hiện (username/userId). */
    private String createdBy;

    /** Có cho âm tồn sau khi điều chỉnh không? */
    private boolean allowNegative = false;

    @Valid
    @NotEmpty
    private List<InventoryAdjustItemDTO> items;

    // getters/setters
    public AdjustmentType getType() { return type; }
    public void setType(AdjustmentType type) { this.type = type; }
    public String getRefType() { return refType; }
    public void setRefType(String refType) { this.refType = refType; }
    public String getRefCode() { return refCode; }
    public void setRefCode(String refCode) { this.refCode = refCode; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public boolean isAllowNegative() { return allowNegative; }
    public void setAllowNegative(boolean allowNegative) { this.allowNegative = allowNegative; }
    public List<InventoryAdjustItemDTO> getItems() { return items; }
    public void setItems(List<InventoryAdjustItemDTO> items) { this.items = items; }
}
