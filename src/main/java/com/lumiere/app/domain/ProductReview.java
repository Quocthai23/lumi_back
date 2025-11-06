package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumiere.app.domain.enumeration.RatingType;
import com.lumiere.app.domain.enumeration.ReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Đánh giá sản phẩm.
 * Frontend: src/types/product.ts (Review)
 */
@Entity
@Table(name = "product_review")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false)
    private RatingType rating;

    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @Lob
    @Column(name = "comment")
    private String comment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "variants", "reviews", "questions", "collections", "wishlistedBies" }, allowSetters = true)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductReview id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RatingType getRating() {
        return this.rating;
    }

    public ProductReview rating(RatingType rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(RatingType rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return this.author;
    }

    public ProductReview author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return this.comment;
    }

    public ProductReview comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReviewStatus getStatus() {
        return this.status;
    }

    public ProductReview status(ReviewStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ProductReview createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductReview product(Product product) {
        this.setProduct(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductReview)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductReview) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductReview{" +
            "id=" + getId() +
            ", rating='" + getRating() + "'" +
            ", author='" + getAuthor() + "'" +
            ", comment='" + getComment() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
