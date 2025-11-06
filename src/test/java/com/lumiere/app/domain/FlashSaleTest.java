package com.lumiere.app.domain;

import static com.lumiere.app.domain.FlashSaleProductTestSamples.*;
import static com.lumiere.app.domain.FlashSaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FlashSaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashSale.class);
        FlashSale flashSale1 = getFlashSaleSample1();
        FlashSale flashSale2 = new FlashSale();
        assertThat(flashSale1).isNotEqualTo(flashSale2);

        flashSale2.setId(flashSale1.getId());
        assertThat(flashSale1).isEqualTo(flashSale2);

        flashSale2 = getFlashSaleSample2();
        assertThat(flashSale1).isNotEqualTo(flashSale2);
    }

    @Test
    void productsTest() {
        FlashSale flashSale = getFlashSaleRandomSampleGenerator();
        FlashSaleProduct flashSaleProductBack = getFlashSaleProductRandomSampleGenerator();

        flashSale.addProducts(flashSaleProductBack);
        assertThat(flashSale.getProducts()).containsOnly(flashSaleProductBack);
        assertThat(flashSaleProductBack.getFlashSale()).isEqualTo(flashSale);

        flashSale.removeProducts(flashSaleProductBack);
        assertThat(flashSale.getProducts()).doesNotContain(flashSaleProductBack);
        assertThat(flashSaleProductBack.getFlashSale()).isNull();

        flashSale.products(new HashSet<>(Set.of(flashSaleProductBack)));
        assertThat(flashSale.getProducts()).containsOnly(flashSaleProductBack);
        assertThat(flashSaleProductBack.getFlashSale()).isEqualTo(flashSale);

        flashSale.setProducts(new HashSet<>());
        assertThat(flashSale.getProducts()).doesNotContain(flashSaleProductBack);
        assertThat(flashSaleProductBack.getFlashSale()).isNull();
    }
}
