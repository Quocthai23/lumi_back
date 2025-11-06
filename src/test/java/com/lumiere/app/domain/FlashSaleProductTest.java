package com.lumiere.app.domain;

import static com.lumiere.app.domain.FlashSaleProductTestSamples.*;
import static com.lumiere.app.domain.FlashSaleTestSamples.*;
import static com.lumiere.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashSaleProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashSaleProduct.class);
        FlashSaleProduct flashSaleProduct1 = getFlashSaleProductSample1();
        FlashSaleProduct flashSaleProduct2 = new FlashSaleProduct();
        assertThat(flashSaleProduct1).isNotEqualTo(flashSaleProduct2);

        flashSaleProduct2.setId(flashSaleProduct1.getId());
        assertThat(flashSaleProduct1).isEqualTo(flashSaleProduct2);

        flashSaleProduct2 = getFlashSaleProductSample2();
        assertThat(flashSaleProduct1).isNotEqualTo(flashSaleProduct2);
    }

    @Test
    void flashSaleTest() {
        FlashSaleProduct flashSaleProduct = getFlashSaleProductRandomSampleGenerator();
        FlashSale flashSaleBack = getFlashSaleRandomSampleGenerator();

        flashSaleProduct.setFlashSale(flashSaleBack);
        assertThat(flashSaleProduct.getFlashSale()).isEqualTo(flashSaleBack);

        flashSaleProduct.flashSale(null);
        assertThat(flashSaleProduct.getFlashSale()).isNull();
    }

    @Test
    void productTest() {
        FlashSaleProduct flashSaleProduct = getFlashSaleProductRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        flashSaleProduct.setProduct(productBack);
        assertThat(flashSaleProduct.getProduct()).isEqualTo(productBack);

        flashSaleProduct.product(null);
        assertThat(flashSaleProduct.getProduct()).isNull();
    }
}
