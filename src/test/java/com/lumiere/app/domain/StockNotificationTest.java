package com.lumiere.app.domain;

import static com.lumiere.app.domain.ProductVariantTestSamples.*;
import static com.lumiere.app.domain.StockNotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockNotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockNotification.class);
        StockNotification stockNotification1 = getStockNotificationSample1();
        StockNotification stockNotification2 = new StockNotification();
        assertThat(stockNotification1).isNotEqualTo(stockNotification2);

        stockNotification2.setId(stockNotification1.getId());
        assertThat(stockNotification1).isEqualTo(stockNotification2);

        stockNotification2 = getStockNotificationSample2();
        assertThat(stockNotification1).isNotEqualTo(stockNotification2);
    }

    @Test
    void productVariantTest() {
        StockNotification stockNotification = getStockNotificationRandomSampleGenerator();
        ProductVariant productVariantBack = getProductVariantRandomSampleGenerator();

        stockNotification.setProductVariant(productVariantBack);
        assertThat(stockNotification.getProductVariant()).isEqualTo(productVariantBack);

        stockNotification.productVariant(null);
        assertThat(stockNotification.getProductVariant()).isNull();
    }
}
