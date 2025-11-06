package com.lumiere.app.domain;

import static com.lumiere.app.domain.ProductVariantTestSamples.*;
import static com.lumiere.app.domain.StockMovementTestSamples.*;
import static com.lumiere.app.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockMovementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockMovement.class);
        StockMovement stockMovement1 = getStockMovementSample1();
        StockMovement stockMovement2 = new StockMovement();
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);

        stockMovement2.setId(stockMovement1.getId());
        assertThat(stockMovement1).isEqualTo(stockMovement2);

        stockMovement2 = getStockMovementSample2();
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);
    }

    @Test
    void productVariantTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        ProductVariant productVariantBack = getProductVariantRandomSampleGenerator();

        stockMovement.setProductVariant(productVariantBack);
        assertThat(stockMovement.getProductVariant()).isEqualTo(productVariantBack);

        stockMovement.productVariant(null);
        assertThat(stockMovement.getProductVariant()).isNull();
    }

    @Test
    void warehouseTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        Warehouse warehouseBack = getWarehouseRandomSampleGenerator();

        stockMovement.setWarehouse(warehouseBack);
        assertThat(stockMovement.getWarehouse()).isEqualTo(warehouseBack);

        stockMovement.warehouse(null);
        assertThat(stockMovement.getWarehouse()).isNull();
    }
}
