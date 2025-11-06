package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlashSaleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlashSaleDTO.class);
        FlashSaleDTO flashSaleDTO1 = new FlashSaleDTO();
        flashSaleDTO1.setId(1L);
        FlashSaleDTO flashSaleDTO2 = new FlashSaleDTO();
        assertThat(flashSaleDTO1).isNotEqualTo(flashSaleDTO2);
        flashSaleDTO2.setId(flashSaleDTO1.getId());
        assertThat(flashSaleDTO1).isEqualTo(flashSaleDTO2);
        flashSaleDTO2.setId(2L);
        assertThat(flashSaleDTO1).isNotEqualTo(flashSaleDTO2);
        flashSaleDTO1.setId(null);
        assertThat(flashSaleDTO1).isNotEqualTo(flashSaleDTO2);
    }
}
