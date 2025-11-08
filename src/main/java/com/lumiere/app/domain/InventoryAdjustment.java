// src/main/java/com/lumi/app/domain/InventoryAdjustment.java
package com.lumiere.app.domain;

import com.lumiere.app.domain.enumeration.AdjustmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "inventory_adjustment",
       indexes = {
           @Index(name = "idx_adj_inventory", columnList = "inventory_id"),
           @Index(name = "idx_adj_batch", columnList = "batch_id"),
           @Index(name = "idx_adj_ref", columnList = "ref_type, ref_code")
       })
@Getter
@Setter
public class InventoryAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Gom nhóm các điều chỉnh trong một đợt bulk (UUID string). */
    @Column(name = "batch_id", length = 64, nullable = false)
    private String batchId;

    /** Tồn kho (variant + warehouse) chịu tác động. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    /** Trước khi điều chỉnh. */
    @Column(name = "qty_before", nullable = false)
    private Long qtyBefore;

    /** Số lượng thay đổi (âm hoặc dương). */
    @Column(name = "qty_delta", nullable = false)
    private Long qtyDelta;

    /** Sau khi điều chỉnh. */
    @Column(name = "qty_after", nullable = false)
    private Long qtyAfter;

    /** Kiểu điều chỉnh. */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 32, nullable = false)
    private AdjustmentType type;

    /** Tham chiếu nghiệp vụ: đơn hàng, phiếu nhập, v.v. */
    @Column(name = "ref_type", length = 64)
    private String refType;

    @Column(name = "ref_code", length = 128)
    private String refCode;

    /** Ghi chú tự do. */
    @Column(name = "note", length = 1000)
    private String note;

    /** Ai làm + khi nào. */
    @Column(name = "created_by", length = 64)
    private String createdBy;

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof InventoryAdjustment that)) return false; return Objects.equals(id, that.id);}
    @Override public int hashCode(){ return Objects.hashCode(id);}
}
