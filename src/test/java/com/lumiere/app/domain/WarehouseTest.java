package com.lumiere.app.domain;

import static com.lumiere.app.domain.WarehouseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WarehouseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Warehouse.class);
        Warehouse warehouse1 = getWarehouseSample1();
        Warehouse warehouse2 = new Warehouse();
        assertThat(warehouse1).isNotEqualTo(warehouse2);

        warehouse2.setId(warehouse1.getId());
        assertThat(warehouse1).isEqualTo(warehouse2);

        warehouse2 = getWarehouseSample2();
        assertThat(warehouse1).isNotEqualTo(warehouse2);
    }
}
