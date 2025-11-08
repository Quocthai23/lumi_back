package com.lumiere.app.service.dto;


import com.lumiere.app.domain.enumeration.AdjustmentType;

import java.util.List;

public record BulkAdjustRequest(
    AdjustmentType type,
    String refType,
    String refCode,
    String createdBy,
    boolean allowNegative,            // Cho phép âm tồn sau điều chỉnh?
    List<InventoryAdjustItem> items
) {}
