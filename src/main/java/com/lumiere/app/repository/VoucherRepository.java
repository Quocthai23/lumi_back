package com.lumiere.app.repository;

import com.lumiere.app.domain.Voucher;
import com.lumiere.app.domain.enumeration.VoucherStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Voucher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    /**
     * Tìm voucher theo code.
     *
     * @param code mã voucher
     * @return voucher nếu tìm thấy
     */
    Optional<Voucher> findByCode(String code);

    /**
     * Tìm tất cả voucher available (ACTIVE và chưa hết hạn).
     *
     * @param status trạng thái ACTIVE
     * @param now thời gian hiện tại
     * @param pageable thông tin phân trang
     * @return page của vouchers
     */
    @Query("SELECT v FROM Voucher v WHERE v.status = :status " +
           "AND (v.startDate IS NULL OR v.startDate <= :now) " +
           "AND (v.endDate IS NULL OR v.endDate >= :now) " +
           "AND (v.usageLimit IS NULL OR v.usageLimit = 0 OR v.usageCount IS NULL OR v.usageCount < v.usageLimit)")
    Page<Voucher> findAllAvailable(
        @Param("status") VoucherStatus status,
        @Param("now") Instant now,
        Pageable pageable
    );

    /**
     * Tìm tất cả voucher ACTIVE đã hết hạn (endDate < now).
     *
     * @param status trạng thái ACTIVE
     * @param now thời gian hiện tại
     * @return danh sách voucher đã hết hạn
     */
    @Query("SELECT v FROM Voucher v WHERE v.status = :status " +
           "AND v.endDate IS NOT NULL " +
           "AND v.endDate < :now")
    List<Voucher> findExpiredVouchers(
        @Param("status") VoucherStatus status,
        @Param("now") Instant now
    );

    /**
     * Tìm tất cả voucher ACTIVE sắp hết hạn trong khoảng thời gian.
     *
     * @param status trạng thái ACTIVE
     * @param start thời gian bắt đầu
     * @param end thời gian kết thúc
     * @return danh sách voucher sắp hết hạn
     */
    @Query("SELECT v FROM Voucher v WHERE v.status = :status " +
           "AND v.endDate IS NOT NULL " +
           "AND v.endDate >= :start " +
           "AND v.endDate <= :end")
    List<Voucher> findVouchersExpiringBetween(
        @Param("status") VoucherStatus status,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
}
