// src/main/java/com/lumi/app/service/dto/InventoryAdjustItem.java
package com.lumiere.app.service.dto;


public record InventoryAdjustItem(
    Long inventoryId,     // Bắt buộc
    Long delta,           // Dương/Âm. Nếu dùng "CORRECTION" thì delta có thể được BE tính lại từ targetQty.
    Long targetQty,       // Tùy chọn: nếu gửi targetQty != null & type=CORRECTION, BE sẽ set về đúng số này.
    String note
) {}
