package com.lumiere.app.repository;

import com.lumiere.app.domain.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    Page<CartItem> findAllByCustomerId(Long customerId, Pageable pageable);
}
