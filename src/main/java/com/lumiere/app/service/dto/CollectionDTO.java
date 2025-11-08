package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.lumiere.app.domain.Collection} entity.
 */
@Schema(description = "Bộ sưu tập (Shop The Look).\nFrontend: src/types/collection.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CollectionDTO implements Serializable {

    private Long id;

    private String name;

    private String slug;

    @Lob
    private String description;

    private String imageUrl;

    private String lookImageUrl;

    private Set<ProductDTO> products = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLookImageUrl() {
        return lookImageUrl;
    }

    public void setLookImageUrl(String lookImageUrl) {
        this.lookImageUrl = lookImageUrl;
    }

    public Set<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductDTO> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionDTO)) {
            return false;
        }

        CollectionDTO collectionDTO = (CollectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, collectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CollectionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", lookImageUrl='" + getLookImageUrl() + "'" +
            ", products=" + getProducts() +
            "}";
    }
}
