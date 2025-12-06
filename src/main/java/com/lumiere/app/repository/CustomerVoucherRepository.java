package com.lumiere.app.repository;

import com.lumiere.app.domain.CustomerVoucher;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CustomerVoucher entity.
 */
@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    /**
     * Tìm voucher đã được tặng cho khách hàng trong quý cụ thể.
     *
     * @param customerId ID của khách hàng
     * @param quarter Quý (format: "2024-Q1")
     * @return CustomerVoucher nếu tìm thấy
     */
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.quarter = :quarter")
    Optional<CustomerVoucher> findByCustomerIdAndQuarter(@Param("customerId") Long customerId, @Param("quarter") String quarter);

    /**
     * Tìm tất cả voucher đã được tặng cho khách hàng.
     *
     * @param customerId ID của khách hàng
     * @return Danh sách CustomerVoucher
     */
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId ORDER BY cv.giftedAt DESC")
    List<CustomerVoucher> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Kiểm tra xem khách hàng đã nhận voucher trong quý này chưa.
     *
     * @param customerId ID của khách hàng
     * @param quarter Quý (format: "2024-Q1")
     * @return true nếu đã nhận, false nếu chưa
     */
    @Query("SELECT COUNT(cv) > 0 FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.quarter = :quarter")
    boolean existsByCustomerIdAndQuarter(@Param("customerId") Long customerId, @Param("quarter") String quarter);

    /**
     * Kiểm tra xem khách hàng đã nhận voucher sinh nhật trong năm này chưa.
     * Kiểm tra bằng cách tìm voucher có quarter bắt đầu bằng năm (format: "2024").
     *
     * @param customerId ID của khách hàng
     * @param yearString Năm dạng string (format: "2024")
     * @return true nếu đã nhận, false nếu chưa
     */
    @Query("SELECT COUNT(cv) > 0 FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.quarter = :yearString")
    boolean existsByCustomerIdAndYear(@Param("customerId") Long customerId, @Param("yearString") String yearString);

    /**
     * Tìm CustomerVoucher theo customer và voucher.
     *
     * @param customerId ID của khách hàng
     * @param voucherId ID của voucher
     * @return CustomerVoucher nếu tìm thấy
     */
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.voucher.id = :voucherId")
    Optional<CustomerVoucher> findByCustomerIdAndVoucherId(@Param("customerId") Long customerId, @Param("voucherId") Long voucherId);
}

