package com.lumiere.app.domain;

import static com.lumiere.app.domain.InventoryTestSamples.*;
import static com.lumiere.app.domain.ProductVariantTestSamples.*;
import static com.lumiere.app.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inventory.class);
        Inventory inventory1 = getInventorySample1();
        Inventory inventory2 = new Inventory();
        assertThat(inventory1).isNotEqualTo(inventory2);

        inventory2.setId(inventory1.getId());
        assertThat(inventory1).isEqualTo(inventory2);

        inventory2 = getInventorySample2();
        assertThat(inventory1).isNotEqualTo(inventory2);
    }

    @Test
    void productVariantTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        ProductVariant productVariantBack = getProductVariantRandomSampleGenerator();

        inventory.setProductVariant(productVariantBack);
        assertThat(inventory.getProductVariant()).isEqualTo(productVariantBack);

        inventory.productVariant(null);
        assertThat(inventory.getProductVariant()).isNull();
    }

    @Test
    void warehouseTest() {
        Inventory inventory = getInventoryRandomSampleGenerator();
        Warehouse warehouseBack = getWarehouseRandomSampleGenerator();

        inventory.setWarehouse(warehouseBack);
        assertThat(inventory.getWarehouse()).isEqualTo(warehouseBack);

        inventory.warehouse(null);
        assertThat(inventory.getWarehouse()).isNull();
    }
}
