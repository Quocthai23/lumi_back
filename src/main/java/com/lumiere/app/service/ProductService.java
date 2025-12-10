package com.lumiere.app.service;

import com.lumiere.app.service.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.Product}.
 */
public interface ProductService {
    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    ProductDTO save(ProductDTO productDTO);

    /**
     * Updates a product.
     *
     * @param productDTO the entity to update.
     * @return the persisted entity.
     */
    ProductDTO update(ProductDTO productDTO);

    @Transactional
    ProductDTO createProductDTO(ProductDTO dto);

    /**
     * Partially updates a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductDTO> partialUpdate(ProductDTO productDTO);

    /**
     * Get the "id" product.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductDTO> findOne(Long id);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Page<ProductDTO> searchProducts(
        List<Long> categoryIds,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Pageable pageable
    );

    /**
     * Lấy tất cả ảnh của product và variants theo variant ID.
     * Key = null hoặc 0L: ảnh chung của product
     * Key = variantId: ảnh riêng của variant đó
     *
     * @param productId ID của product
     * @return Map với key là variantId (hoặc null cho ảnh chung), value là URL ảnh
     */
    Map<Long, String> getProductImagesMapByVariantId(Long productId);

}
