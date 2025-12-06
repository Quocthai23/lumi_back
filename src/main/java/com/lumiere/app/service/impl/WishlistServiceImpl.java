package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.WishlistItem;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.repository.WishlistItemRepository;
import com.lumiere.app.service.CartItemService;
import com.lumiere.app.service.WishlistService;
import com.lumiere.app.service.dto.CartItemDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.dto.WishlistItemDTO;
import com.lumiere.app.service.mapper.ProductVariantMapper;
import com.lumiere.app.service.mapper.WishlistItemMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishRepo;
    private final ProductVariantRepository variantRepo;
    private final WishlistItemMapper mapper;
    private final CartItemService cartService; // optional, inject if available
    private final ProductVariantMapper productVariantMapper;

    public WishlistServiceImpl(
        WishlistItemRepository wishRepo,
        ProductVariantRepository variantRepo,
        WishlistItemMapper mapper,
        Optional<CartItemService> cartService, ProductVariantMapper productVariantMapper
    ) {
        this.wishRepo = wishRepo;
        this.variantRepo = variantRepo;
        this.mapper = mapper;
        this.cartService = cartService.orElse(null);
        this.productVariantMapper = productVariantMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistItemDTO> findAllByUserId(Long userId, Pageable pageable) {
        Page<WishlistItem> page = wishRepo.findAllByUserId(userId, pageable);
        Set<Long> variantIds = page.getContent().stream()
            .map(WishlistItem::getVariantId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, ProductVariant> vmap = variantRepo.findAllByIdIn(variantIds).stream()
            .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        page.getContent().forEach(w -> w.setProductVariant(vmap.get(w.getVariantId())));

        return page.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemDTO> findAllByUserId(Long userId) {
        List<WishlistItem> list = wishRepo.findAllByUserId(userId);
        if (list.isEmpty()) return Collections.emptyList();
        Set<Long> variantIds = list.stream().map(WishlistItem::getVariantId).collect(Collectors.toSet());
        Map<Long, ProductVariantDTO> vmap = variantRepo.findAllByIdIn(variantIds).stream().map(productVariantMapper::toDto)
            .collect(Collectors.toMap(ProductVariantDTO::getId, v -> v));
        List<WishlistItemDTO> rs = list.stream().map(mapper::toDto).toList();
        rs.forEach(w -> w.setVariant(vmap.get(w.getVariantId())));
        return rs;
    }

    @Override
    public WishlistItemDTO addToWishlist(Long userId, Long variantId) {
        Optional<WishlistItem> exist = wishRepo.findByUserIdAndVariantId(userId, variantId);
        if (exist.isPresent()) {
            WishlistItem w = exist.get();
            variantRepo.findById(variantId).ifPresent(w::setProductVariant);
            return mapper.toDto(w);
        }
        WishlistItem w = new WishlistItem();
        w.setUserId(userId);
        w.setVariantId(variantId);
        w.setCreatedAt(Instant.now());
        WishlistItem saved = wishRepo.save(w);
        variantRepo.findById(variantId).ifPresent(saved::setProductVariant);
        return mapper.toDto(saved);
    }

    @Override
    public void removeFromWishlist(Long userId, Long variantId) {
        wishRepo.deleteByUserIdAndVariantId(userId, variantId);
    }

    @Override
    public CartItemDTO moveToCart(Long userId, Long variantId, int qty) {
        wishRepo.findByUserIdAndVariantId(userId, variantId).ifPresent(wishRepo::delete);

        if (cartService == null) {
            throw new IllegalStateException("CartService not available for moveToCart");
        }

        // delegate to cart service (assumed thread-safe & transactional)
        return cartService.addToCart(userId, variantId, qty); // assume returns CartItem DTO; adapt if not
    }
}
