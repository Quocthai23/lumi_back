package com.lumiere.app.repository;

import com.lumiere.app.domain.Voucher;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
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
}
