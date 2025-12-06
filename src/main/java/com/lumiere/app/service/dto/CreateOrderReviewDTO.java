package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.RatingType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO cho việc tạo review cho một sản phẩm trong đơn hàng.
 */
public class CreateOrderReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long orderItemId;

    @NotNull
    private RatingType rating;

    private String comment;

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public RatingType getRating() {
        return rating;
    }

    public void setRating(RatingType rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

