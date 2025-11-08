package com.lumiere.app.service;


import com.lumiere.app.service.dto.BulkAdjustRequestDTO;
import com.lumiere.app.service.dto.BulkAdjustResponseDTO;

public interface InventoryBulkService {
    BulkAdjustResponseDTO bulkAdjust(BulkAdjustRequestDTO req);
}
