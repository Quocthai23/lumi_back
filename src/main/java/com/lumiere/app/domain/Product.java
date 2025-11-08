package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumiere.app.domain.enumeration.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Thực thể Product, đại diện cho một mặt hàng.
 * Frontend: src/types/product.ts
 * @filter
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Size(min = 2)
    @Column(name = "slug")
    private String slug;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "category")
    private String category;

    @Column(name = "material")
    private String material;

    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    @Column(name = "average_rating")
    private Double averageRating;

    @Min(value = 0)
    @Column(name = "review_count")
    private Integer reviewCount;

    @Lob
    @Column(name = "images")
    private String images;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductVariant> variants = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductReview> reviews = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "answers" }, allowSetters = true)
    private Set<ProductQuestion> questions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "products")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<Collection> collections = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "wishlists")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "orders", "wishlists", "addresses", "loyaltyHistories", "notifications" }, allowSetters = true)
    private Set<Customer> wishlistedBies = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Product code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Product slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductStatus getStatus() {
        return this.status;
    }

    public Product status(ProductStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public String getCategory() {
        return this.category;
    }

    public Product category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return this.material;
    }

    public Product material(String material) {
        this.setMaterial(material);
        return this;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Double getAverageRating() {
        return this.averageRating;
    }

    public Product averageRating(Double averageRating) {
        this.setAverageRating(averageRating);
        return this;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return this.reviewCount;
    }

    public Product reviewCount(Integer reviewCount) {
        this.setReviewCount(reviewCount);
        return this;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getImages() {
        return this.images;
    }

    public Product images(String images) {
        this.setImages(images);
        return this;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Product createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Product updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ProductVariant> getVariants() {
        return this.variants;
    }

    public void setVariants(Set<ProductVariant> productVariants) {
        if (this.variants != null) {
            this.variants.forEach(i -> i.setProduct(null));
        }
        if (productVariants != null) {
            productVariants.forEach(i -> i.setProduct(this));
        }
        this.variants = productVariants;
    }

    public Product variants(Set<ProductVariant> productVariants) {
        this.setVariants(productVariants);
        return this;
    }

    public Product addVariants(ProductVariant productVariant) {
        this.variants.add(productVariant);
        productVariant.setProduct(this);
        return this;
    }

    public Product removeVariants(ProductVariant productVariant) {
        this.variants.remove(productVariant);
        productVariant.setProduct(null);
        return this;
    }

    public Set<ProductReview> getReviews() {
        return this.reviews;
    }

    public void setReviews(Set<ProductReview> productReviews) {
        if (this.reviews != null) {
            this.reviews.forEach(i -> i.setProduct(null));
        }
        if (productReviews != null) {
            productReviews.forEach(i -> i.setProduct(this));
        }
        this.reviews = productReviews;
    }

    public Product reviews(Set<ProductReview> productReviews) {
        this.setReviews(productReviews);
        return this;
    }

    public Product addReviews(ProductReview productReview) {
        this.reviews.add(productReview);
        productReview.setProduct(this);
        return this;
    }

    public Product removeReviews(ProductReview productReview) {
        this.reviews.remove(productReview);
        productReview.setProduct(null);
        return this;
    }

    public Set<ProductQuestion> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<ProductQuestion> productQuestions) {
        if (this.questions != null) {
            this.questions.forEach(i -> i.setProduct(null));
        }
        if (productQuestions != null) {
            productQuestions.forEach(i -> i.setProduct(this));
        }
        this.questions = productQuestions;
    }

    public Product questions(Set<ProductQuestion> productQuestions) {
        this.setQuestions(productQuestions);
        return this;
    }

    public Product addQuestions(ProductQuestion productQuestion) {
        this.questions.add(productQuestion);
        productQuestion.setProduct(this);
        return this;
    }

    public Product removeQuestions(ProductQuestion productQuestion) {
        this.questions.remove(productQuestion);
        productQuestion.setProduct(null);
        return this;
    }

    public Set<Collection> getCollections() {
        return this.collections;
    }

    public void setCollections(Set<Collection> collections) {
        if (this.collections != null) {
            this.collections.forEach(i -> i.removeProducts(this));
        }
        if (collections != null) {
            collections.forEach(i -> i.addProducts(this));
        }
        this.collections = collections;
    }

    public Product collections(Set<Collection> collections) {
        this.setCollections(collections);
        return this;
    }

    public Product addCollections(Collection collection) {
        this.collections.add(collection);
        collection.getProducts().add(this);
        return this;
    }

    public Product removeCollections(Collection collection) {
        this.collections.remove(collection);
        collection.getProducts().remove(this);
        return this;
    }

    public Set<Customer> getWishlistedBies() {
        return this.wishlistedBies;
    }

    public void setWishlistedBies(Set<Customer> customers) {
        if (this.wishlistedBies != null) {
            this.wishlistedBies.forEach(i -> i.removeWishlist(this));
        }
        if (customers != null) {
            customers.forEach(i -> i.addWishlist(this));
        }
        this.wishlistedBies = customers;
    }

    public Product wishlistedBies(Set<Customer> customers) {
        this.setWishlistedBies(customers);
        return this;
    }

    public Product addWishlistedBy(Customer customer) {
        this.wishlistedBies.add(customer);
        customer.getWishlists().add(this);
        return this;
    }

    public Product removeWishlistedBy(Customer customer) {
        this.wishlistedBies.remove(customer);
        customer.getWishlists().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
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
            "}";
    }
}
