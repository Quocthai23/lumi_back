package com.lumiere.app.service.dto;

import java.util.List;

public record BulkAdjustResponse(
    String batchId,
    int affected,                     // số inventory được cập nhật
    List<Long> updatedInventoryIds
) {}
