package com.lumiere.app.service;

import com.lumiere.app.service.dto.CartItemDTO;
import com.lumiere.app.service.dto.WishlistItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface WishlistService {
    Page<WishlistItemDTO> findAllByUserId(Long userId, Pageable pageable);
    List<WishlistItemDTO> findAllByUserId(Long userId);
    WishlistItemDTO addToWishlist(Long userId, Long variantId);
    void removeFromWishlist(Long userId, Long variantId);
    CartItemDTO moveToCart(Long userId, Long variantId, int qty);
}
