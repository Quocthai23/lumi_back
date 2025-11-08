// src/main/java/com/lumi/app/service/dto/BulkAdjustResponseDTO.java
package com.lumiere.app.service.dto;

import java.util.List;

public class BulkAdjustResponseDTO {

    private String batchId;
    private int affected;
    private List<Long> updatedInventoryIds;

    public BulkAdjustResponseDTO() { }

    public BulkAdjustResponseDTO(String batchId, int affected, List<Long> updatedInventoryIds) {
        this.batchId = batchId;
        this.affected = affected;
        this.updatedInventoryIds = updatedInventoryIds;
    }

    // getters/setters
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public int getAffected() { return affected; }
    public void setAffected(int affected) { this.affected = affected; }
    public List<Long> getUpdatedInventoryIds() { return updatedInventoryIds; }
    public void setUpdatedInventoryIds(List<Long> updatedInventoryIds) { this.updatedInventoryIds = updatedInventoryIds; }
}
