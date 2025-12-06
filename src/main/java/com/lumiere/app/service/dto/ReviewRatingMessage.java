package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.RatingType;
import java.io.Serializable;

/**
 * Kafka message để tính điểm review cho sản phẩm.
 */
public class ReviewRatingMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private Long reviewId;
    private RatingType rating;
    private Long customerId;
    private Long orderId;

    public ReviewRatingMessage() {
    }

    public ReviewRatingMessage(Long productId, Long reviewId, RatingType rating, Long customerId, Long orderId) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.rating = rating;
        this.customerId = customerId;
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public RatingType getRating() {
        return rating;
    }

    public void setRating(RatingType rating) {
        this.rating = rating;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "ReviewRatingMessage{" +
            "productId=" + productId +
            ", reviewId=" + reviewId +
            ", rating=" + rating +
            ", customerId=" + customerId +
            ", orderId=" + orderId +
            '}';
    }
}

