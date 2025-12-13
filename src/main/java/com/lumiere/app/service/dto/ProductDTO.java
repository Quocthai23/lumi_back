package com.lumiere.app.service.dto;

import com.lumiere.app.domain.ProductAttachment;
import com.lumiere.app.domain.enumeration.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.lumiere.app.domain.Product} entity.
 */
@Schema(description = "Thực thể Product, đại diện cho một mặt hàng.\nFrontend: src/types/product.ts\n@filter")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProductDTO implements Serializable {

    private Long id;

    private String code;

    private String name;

    private String slug;

    @Lob
    private String description;

    private ProductStatus status;

    private String category;

    private String material;

    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    private Double averageRating;

    @Min(value = 0)
    private Integer reviewCount;

    @Lob
    private String images;

    private Instant createdAt;

    private Instant updatedAt;

    private Long categoryId;

    private Set<ProductAttachment> productAttachments = new HashSet<>();

    private Set<AttachmentDTO> attachmentDTOS;

    private Set<CollectionDTO> collections = new HashSet<>();

    private Set<CustomerDTO> wishlistedBies = new HashSet<>();

    private BigDecimal price;

    private BigDecimal promotionPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", category='" + getCategory() + "'" +
            ", material='" + getMaterial() + "'" +
            ", averageRating=" + getAverageRating() +
            ", reviewCount=" + getReviewCount() +
            ", images='" + getImages() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", collections=" + getCollections() +
            ", wishlistedBies=" + getWishlistedBies() +
            "}";
    }
}
