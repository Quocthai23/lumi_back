package com.lumiere.app.service.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistItemDTO {

    private Long id;
    private Long userId;
    private Long variantId;
    private Instant createdAt;

    private Long variantPrice;
    private Long variantStock;
    private String sku;

    private ProductVariantDTO variant;
}
