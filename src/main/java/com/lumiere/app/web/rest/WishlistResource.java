package com.lumiere.app.web.rest;


import com.lumiere.app.security.SecurityUtils;
import com.lumiere.app.service.WishlistService;
import com.lumiere.app.service.dto.WishlistItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class WishlistResource {

    private final WishlistService wishlistService;

    public WishlistResource(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<WishlistItemDTO>> getWishlist(Pageable pageable) {
        Optional<Long> optUserId = SecurityUtils.getCurrentUserId();
        if (optUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = optUserId.get();

        List<WishlistItemDTO> page = wishlistService.findAllByUserId(userId);
        return ResponseEntity.ok()
            .body(page);
    }

    @PostMapping("/wishlist")
    public ResponseEntity<WishlistItemDTO> addToWishlist(@RequestParam Long variantId) {
        Optional<Long> optUserId = SecurityUtils.getCurrentUserId();
        if (optUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = optUserId.get();

        WishlistItemDTO dto = wishlistService.addToWishlist(userId, variantId);
        return ResponseEntity.created(URI.create("/api/wishlist/" + dto.getId())).body(dto);
    }

    @DeleteMapping("/wishlist")
    public ResponseEntity<Void> removeFromWishlist(@RequestParam Long variantId) {
        Optional<Long> optUserId = SecurityUtils.getCurrentUserId();
        if (optUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = optUserId.get();

        wishlistService.removeFromWishlist(userId, variantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wishlist/move-to-cart")
    public ResponseEntity<?> moveToCart(@RequestParam Long variantId,
                                        @RequestParam(defaultValue = "1") int qty) {
        Optional<Long> optUserId = SecurityUtils.getCurrentUserId();
        if (optUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = optUserId.get();

        Object cartResult = wishlistService.moveToCart(userId, variantId, qty);
        return ResponseEntity.ok(cartResult);
    }
}
