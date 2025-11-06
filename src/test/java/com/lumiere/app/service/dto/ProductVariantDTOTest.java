package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductVariantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVariantDTO.class);
        ProductVariantDTO productVariantDTO1 = new ProductVariantDTO();
        productVariantDTO1.setId(1L);
        ProductVariantDTO productVariantDTO2 = new ProductVariantDTO();
        assertThat(productVariantDTO1).isNotEqualTo(productVariantDTO2);
        productVariantDTO2.setId(productVariantDTO1.getId());
        assertThat(productVariantDTO1).isEqualTo(productVariantDTO2);
        productVariantDTO2.setId(2L);
        assertThat(productVariantDTO1).isNotEqualTo(productVariantDTO2);
        productVariantDTO1.setId(null);
        assertThat(productVariantDTO1).isNotEqualTo(productVariantDTO2);
    }
}
