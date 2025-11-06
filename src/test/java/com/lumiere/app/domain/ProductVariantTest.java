package com.lumiere.app.domain;

import static com.lumiere.app.domain.ProductTestSamples.*;
import static com.lumiere.app.domain.ProductVariantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductVariantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVariant.class);
        ProductVariant productVariant1 = getProductVariantSample1();
        ProductVariant productVariant2 = new ProductVariant();
        assertThat(productVariant1).isNotEqualTo(productVariant2);

        productVariant2.setId(productVariant1.getId());
        assertThat(productVariant1).isEqualTo(productVariant2);

        productVariant2 = getProductVariantSample2();
        assertThat(productVariant1).isNotEqualTo(productVariant2);
    }

    @Test
    void productTest() {
        ProductVariant productVariant = getProductVariantRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productVariant.setProduct(productBack);
        assertThat(productVariant.getProduct()).isEqualTo(productBack);

        productVariant.product(null);
        assertThat(productVariant.getProduct()).isNull();
    }
}
