// src/main/java/com/lumi/app/service/dto/InventoryAdjustItemDTO.java
package com.lumiere.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InventoryAdjustItemDTO {

    @NotNull
    private Long inventoryId;

    /** Số cộng/trừ (âm/dương). Nếu dùng CORRECTION + targetQty != null thì có thể bỏ trống delta. */
    private Long delta;

    /** Số tồn mục tiêu khi chốt tồn (CORRECTION). */
    private Long targetQty;

    @Size(max = 1000)
    private String note;

    // getters/setters
    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }
    public Long getDelta() { return delta; }
    public void setDelta(Long delta) { this.delta = delta; }
    public Long getTargetQty() { return targetQty; }
    public void setTargetQty(Long targetQty) { this.targetQty = targetQty; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
