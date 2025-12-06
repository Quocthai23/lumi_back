package com.lumiere.app.web.rest;

import com.lumiere.app.service.InventoryBulkService;
import com.lumiere.app.service.dto.BulkAdjustRequestDTO;
import com.lumiere.app.service.dto.BulkAdjustResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryBulkResource {

    private final InventoryBulkService bulkService;

    @PostMapping("/bulk-adjust")
    public ResponseEntity<BulkAdjustResponseDTO> bulkAdjust(@Valid @RequestBody BulkAdjustRequestDTO req) {
        return ResponseEntity.ok(bulkService.bulkAdjust(req));
    }
}
