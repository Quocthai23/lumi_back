package com.lumiere.app.repository;

import com.lumiere.app.domain.FlashSale;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlashSale entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {}
