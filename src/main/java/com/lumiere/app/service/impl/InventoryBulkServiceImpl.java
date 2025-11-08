package com.lumiere.app.service.impl;
import com.lumiere.app.domain.Inventory;
import com.lumiere.app.domain.InventoryAdjustment;
import com.lumiere.app.domain.enumeration.AdjustmentType;
import com.lumiere.app.repository.InventoryAdjustmentRepository;
import com.lumiere.app.repository.InventoryRepository;
import com.lumiere.app.service.InventoryBulkService;
import com.lumiere.app.service.dto.BulkAdjustRequestDTO;
import com.lumiere.app.service.dto.BulkAdjustResponseDTO;
import com.lumiere.app.service.dto.InventoryAdjustItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryBulkServiceImpl implements InventoryBulkService {

    private final InventoryRepository inventoryRepository;
    private final InventoryAdjustmentRepository adjustmentRepository;

    @Override
    @Transactional
    public BulkAdjustResponseDTO bulkAdjust(BulkAdjustRequestDTO req) {
        final String batchId = UUID.randomUUID().toString().replace("-", "");
        final Instant now = Instant.now();

        // 1) Gộp item theo inventoryId (nếu UI gửi trùng)
        Map<Long, InventoryAdjustItemDTO> merged = mergeItems(req.getItems());

        // 2) Khóa inventory cần cập nhật
        List<Long> ids = new ArrayList<>(merged.keySet());
        if (ids.isEmpty()) return new BulkAdjustResponseDTO(batchId, 0, List.of());

        List<Inventory> inventories = inventoryRepository.findAllByIdForUpdate(ids);

        // 3) Tính before/after & validate
        List<InventoryAdjustment> logs = new ArrayList<>(inventories.size());
        List<Long> updatedIds = new ArrayList<>(inventories.size());

        for (Inventory inv : inventories) {
            InventoryAdjustItemDTO it = merged.get(inv.getId());
            if (it == null) continue;

            long before = inv.getStockQuantity() == null ? 0L : inv.getStockQuantity();
            long after;

            if (req.getType() == AdjustmentType.CORRECTION && it.getTargetQty() != null) {
                after = it.getTargetQty();
            } else {
                long delta = it.getDelta() == null ? 0L : it.getDelta();
                after = before + delta;
            }

            if (!req.isAllowNegative() && after < 0) {
                throw new IllegalStateException("Inventory #" + inv.getId() + " âm (" + after + ") không được phép.");
            }

            inv.setStockQuantity(after);
            updatedIds.add(inv.getId());

            InventoryAdjustment logEntity = new InventoryAdjustment();
            logEntity.setBatchId(batchId);
            logEntity.setInventory(inv);
            logEntity.setQtyBefore(before);
            logEntity.setQtyAfter(after);
            logEntity.setQtyDelta(after - before);
            logEntity.setType(req.getType());
            logEntity.setRefType(req.getRefType());
            logEntity.setRefCode(req.getRefCode());
            logEntity.setNote(it.getNote());
            logEntity.setCreatedBy(req.getCreatedBy());
            logEntity.setCreatedAt(now);

            logs.add(logEntity);
        }

        // 4) Ghi DB (batch insert/update nếu đã bật hibernate.jdbc.batch_size)
        inventoryRepository.saveAll(inventories);
        adjustmentRepository.saveAll(logs);

        log.info("Bulk adjust done: batchId={}, affected={}", batchId, updatedIds.size());
        return new BulkAdjustResponseDTO(batchId, updatedIds.size(), updatedIds);
    }

    /** Gộp theo inventoryId: cộng delta, giữ targetQty cuối cùng, note lấy của item sau. */
    private Map<Long, InventoryAdjustItemDTO> mergeItems(List<InventoryAdjustItemDTO> items) {
        Map<Long, InventoryAdjustItemDTO> merged = new LinkedHashMap<>();
        if (items == null) return merged;

        for (InventoryAdjustItemDTO i : items) {
            merged.merge(i.getInventoryId(), i, (a, b) -> {
                Long d = (a.getDelta() == null ? 0L : a.getDelta()) + (b.getDelta() == null ? 0L : b.getDelta());
                InventoryAdjustItemDTO m = new InventoryAdjustItemDTO();
                m.setInventoryId(a.getInventoryId());
                m.setDelta(d);
                m.setTargetQty(b.getTargetQty() != null ? b.getTargetQty() : a.getTargetQty());
                m.setNote(b.getNote()); // prefer note cuối
                return m;
            });
        }
        return merged;
    }
}
