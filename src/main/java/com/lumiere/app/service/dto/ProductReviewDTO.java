package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.RatingType;
import com.lumiere.app.domain.enumeration.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.ProductReview} entity.
 */
@Schema(description = "Đánh giá sản phẩm.\nFrontend: src/types/product.ts (Review)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductReviewDTO implements Serializable {

    private Long id;

    @NotNull
    private RatingType rating;

    @NotNull
    private String author;

    @Lob
    private String comment;

    @NotNull
    private ReviewStatus status;

    @NotNull
    private Instant createdAt;

    @Lob
    private String reply;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RatingType getRating() {
        return rating;
    }

    public void setRating(RatingType rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductReviewDTO)) {
            return false;
        }

        ProductReviewDTO productReviewDTO = (ProductReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductReviewDTO{" +
            "id=" + getId() +
            ", rating='" + getRating() + "'" +
            ", author='" + getAuthor() + "'" +
            ", comment='" + getComment() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", reply='" + getReply() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
