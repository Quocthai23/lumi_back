package com.lumiere.app.repository;

import com.lumiere.app.domain.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Page<WishlistItem> findAllByUserId(Long userId, Pageable pageable);
    List<WishlistItem> findAllByUserId(Long userId);
    Optional<WishlistItem> findByUserIdAndVariantId(Long userId, Long variantId);
    void deleteByUserIdAndVariantId(Long userId, Long variantId);
}
