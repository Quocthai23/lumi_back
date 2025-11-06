package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashSaleProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashSaleProductDTO.class);
        FlashSaleProductDTO flashSaleProductDTO1 = new FlashSaleProductDTO();
        flashSaleProductDTO1.setId(1L);
        FlashSaleProductDTO flashSaleProductDTO2 = new FlashSaleProductDTO();
        assertThat(flashSaleProductDTO1).isNotEqualTo(flashSaleProductDTO2);
        flashSaleProductDTO2.setId(flashSaleProductDTO1.getId());
        assertThat(flashSaleProductDTO1).isEqualTo(flashSaleProductDTO2);
        flashSaleProductDTO2.setId(2L);
        assertThat(flashSaleProductDTO1).isNotEqualTo(flashSaleProductDTO2);
        flashSaleProductDTO1.setId(null);
        assertThat(flashSaleProductDTO1).isNotEqualTo(flashSaleProductDTO2);
    }
}
