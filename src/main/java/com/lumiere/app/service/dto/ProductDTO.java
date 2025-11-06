package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.lumiere.app.domain.Product} entity.
 */
@Schema(description = "Thực thể Product, đại diện cho một mặt hàng.\nFrontend: src/types/product.ts\n@filter")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 64)
    private String code;

    @NotNull
    @Size(min = 2, max = 200)
    private String name;

    @NotNull
    @Size(min = 2)
    private String slug;

    @Lob
    private String description;

    @NotNull
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

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Set<CollectionDTO> collections = new HashSet<>();

    private Set<CustomerDTO> wishlistedBies = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<CollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(Set<CollectionDTO> collections) {
        this.collections = collections;
    }

    public Set<CustomerDTO> getWishlistedBies() {
        return wishlistedBies;
    }

    public void setWishlistedBies(Set<CustomerDTO> wishlistedBies) {
        this.wishlistedBies = wishlistedBies;
    }

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
