package com.lumiere.app.domain;

import static com.lumiere.app.domain.ProductReviewTestSamples.*;
import static com.lumiere.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductReview.class);
        ProductReview productReview1 = getProductReviewSample1();
        ProductReview productReview2 = new ProductReview();
        assertThat(productReview1).isNotEqualTo(productReview2);

        productReview2.setId(productReview1.getId());
        assertThat(productReview1).isEqualTo(productReview2);

        productReview2 = getProductReviewSample2();
        assertThat(productReview1).isNotEqualTo(productReview2);
    }

    @Test
    void productTest() {
        ProductReview productReview = getProductReviewRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productReview.setProduct(productBack);
        assertThat(productReview.getProduct()).isEqualTo(productBack);

        productReview.product(null);
        assertThat(productReview.getProduct()).isNull();
    }
}
