package com.lumiere.app.repository;

import com.lumiere.app.domain.FlashSale;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlashSale entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {
    /**
     * Tìm các flash sale đang diễn ra (startTime <= now <= endTime)
     */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime <= :now AND fs.endTime >= :now ORDER BY fs.startTime ASC")
    List<FlashSale> findActiveFlashSales(@Param("now") Instant now);

    /**
     * Tìm các flash sale sắp diễn ra (startTime > now)
     */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime > :now ORDER BY fs.startTime ASC")
    List<FlashSale> findUpcomingFlashSales(@Param("now") Instant now);

    /**
     * Tìm các flash sale đã kết thúc (endTime < now)
     */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.endTime < :now ORDER BY fs.endTime DESC")
    List<FlashSale> findEndedFlashSales(@Param("now") Instant now);

    /**
     * Tìm flash sale đang diễn ra đầu tiên
     */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime <= :now AND fs.endTime >= :now ORDER BY fs.startTime ASC")
    List<FlashSale> findCurrentFlashSale(@Param("now") Instant now);
}
