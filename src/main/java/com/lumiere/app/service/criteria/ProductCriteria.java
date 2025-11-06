package com.lumiere.app.service.criteria;

import com.lumiere.app.domain.enumeration.ProductStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumiere.app.domain.Product} entity. This class is used
 * in {@link com.lumiere.app.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProductStatus
     */
    public static class ProductStatusFilter extends Filter<ProductStatus> {

        public ProductStatusFilter() {}

        public ProductStatusFilter(ProductStatusFilter filter) {
            super(filter);
        }

        @Override
        public ProductStatusFilter copy() {
            return new ProductStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private StringFilter slug;

    private ProductStatusFilter status;

    private StringFilter category;

    private StringFilter material;

    private DoubleFilter averageRating;

    private IntegerFilter reviewCount;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter variantsId;

    private LongFilter reviewsId;

    private LongFilter questionsId;

    private LongFilter collectionsId;

    private LongFilter wishlistedById;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ProductStatusFilter::copy).orElse(null);
        this.category = other.optionalCategory().map(StringFilter::copy).orElse(null);
        this.material = other.optionalMaterial().map(StringFilter::copy).orElse(null);
        this.averageRating = other.optionalAverageRating().map(DoubleFilter::copy).orElse(null);
        this.reviewCount = other.optionalReviewCount().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.variantsId = other.optionalVariantsId().map(LongFilter::copy).orElse(null);
        this.reviewsId = other.optionalReviewsId().map(LongFilter::copy).orElse(null);
        this.questionsId = other.optionalQuestionsId().map(LongFilter::copy).orElse(null);
        this.collectionsId = other.optionalCollectionsId().map(LongFilter::copy).orElse(null);
        this.wishlistedById = other.optionalWishlistedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public ProductStatusFilter getStatus() {
        return status;
    }

    public Optional<ProductStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ProductStatusFilter status() {
        if (status == null) {
            setStatus(new ProductStatusFilter());
        }
        return status;
    }

    public void setStatus(ProductStatusFilter status) {
        this.status = status;
    }

    public StringFilter getCategory() {
        return category;
    }

    public Optional<StringFilter> optionalCategory() {
        return Optional.ofNullable(category);
    }

    public StringFilter category() {
        if (category == null) {
            setCategory(new StringFilter());
        }
        return category;
    }

    public void setCategory(StringFilter category) {
        this.category = category;
    }

    public StringFilter getMaterial() {
        return material;
    }

    public Optional<StringFilter> optionalMaterial() {
        return Optional.ofNullable(material);
    }

    public StringFilter material() {
        if (material == null) {
            setMaterial(new StringFilter());
        }
        return material;
    }

    public void setMaterial(StringFilter material) {
        this.material = material;
    }

    public DoubleFilter getAverageRating() {
        return averageRating;
    }

    public Optional<DoubleFilter> optionalAverageRating() {
        return Optional.ofNullable(averageRating);
    }

    public DoubleFilter averageRating() {
        if (averageRating == null) {
            setAverageRating(new DoubleFilter());
        }
        return averageRating;
    }

    public void setAverageRating(DoubleFilter averageRating) {
        this.averageRating = averageRating;
    }

    public IntegerFilter getReviewCount() {
        return reviewCount;
    }

    public Optional<IntegerFilter> optionalReviewCount() {
        return Optional.ofNullable(reviewCount);
    }

    public IntegerFilter reviewCount() {
        if (reviewCount == null) {
            setReviewCount(new IntegerFilter());
        }
        return reviewCount;
    }

    public void setReviewCount(IntegerFilter reviewCount) {
        this.reviewCount = reviewCount;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getVariantsId() {
        return variantsId;
    }

    public Optional<LongFilter> optionalVariantsId() {
        return Optional.ofNullable(variantsId);
    }

    public LongFilter variantsId() {
        if (variantsId == null) {
            setVariantsId(new LongFilter());
        }
        return variantsId;
    }

    public void setVariantsId(LongFilter variantsId) {
        this.variantsId = variantsId;
    }

    public LongFilter getReviewsId() {
        return reviewsId;
    }

    public Optional<LongFilter> optionalReviewsId() {
        return Optional.ofNullable(reviewsId);
    }

    public LongFilter reviewsId() {
        if (reviewsId == null) {
            setReviewsId(new LongFilter());
        }
        return reviewsId;
    }

    public void setReviewsId(LongFilter reviewsId) {
        this.reviewsId = reviewsId;
    }

    public LongFilter getQuestionsId() {
        return questionsId;
    }

    public Optional<LongFilter> optionalQuestionsId() {
        return Optional.ofNullable(questionsId);
    }

    public LongFilter questionsId() {
        if (questionsId == null) {
            setQuestionsId(new LongFilter());
        }
        return questionsId;
    }

    public void setQuestionsId(LongFilter questionsId) {
        this.questionsId = questionsId;
    }

    public LongFilter getCollectionsId() {
        return collectionsId;
    }

    public Optional<LongFilter> optionalCollectionsId() {
        return Optional.ofNullable(collectionsId);
    }

    public LongFilter collectionsId() {
        if (collectionsId == null) {
            setCollectionsId(new LongFilter());
        }
        return collectionsId;
    }

    public void setCollectionsId(LongFilter collectionsId) {
        this.collectionsId = collectionsId;
    }

    public LongFilter getWishlistedById() {
        return wishlistedById;
    }

    public Optional<LongFilter> optionalWishlistedById() {
        return Optional.ofNullable(wishlistedById);
    }

    public LongFilter wishlistedById() {
        if (wishlistedById == null) {
            setWishlistedById(new LongFilter());
        }
        return wishlistedById;
    }

    public void setWishlistedById(LongFilter wishlistedById) {
        this.wishlistedById = wishlistedById;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(status, that.status) &&
            Objects.equals(category, that.category) &&
            Objects.equals(material, that.material) &&
            Objects.equals(averageRating, that.averageRating) &&
            Objects.equals(reviewCount, that.reviewCount) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(variantsId, that.variantsId) &&
            Objects.equals(reviewsId, that.reviewsId) &&
            Objects.equals(questionsId, that.questionsId) &&
            Objects.equals(collectionsId, that.collectionsId) &&
            Objects.equals(wishlistedById, that.wishlistedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            name,
            slug,
            status,
            category,
            material,
            averageRating,
            reviewCount,
            createdAt,
            updatedAt,
            variantsId,
            reviewsId,
            questionsId,
            collectionsId,
            wishlistedById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCategory().map(f -> "category=" + f + ", ").orElse("") +
            optionalMaterial().map(f -> "material=" + f + ", ").orElse("") +
            optionalAverageRating().map(f -> "averageRating=" + f + ", ").orElse("") +
            optionalReviewCount().map(f -> "reviewCount=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalVariantsId().map(f -> "variantsId=" + f + ", ").orElse("") +
            optionalReviewsId().map(f -> "reviewsId=" + f + ", ").orElse("") +
            optionalQuestionsId().map(f -> "questionsId=" + f + ", ").orElse("") +
            optionalCollectionsId().map(f -> "collectionsId=" + f + ", ").orElse("") +
            optionalWishlistedById().map(f -> "wishlistedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
