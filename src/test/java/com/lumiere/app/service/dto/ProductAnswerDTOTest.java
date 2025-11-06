package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductAnswerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductAnswerDTO.class);
        ProductAnswerDTO productAnswerDTO1 = new ProductAnswerDTO();
        productAnswerDTO1.setId(1L);
        ProductAnswerDTO productAnswerDTO2 = new ProductAnswerDTO();
        assertThat(productAnswerDTO1).isNotEqualTo(productAnswerDTO2);
        productAnswerDTO2.setId(productAnswerDTO1.getId());
        assertThat(productAnswerDTO1).isEqualTo(productAnswerDTO2);
        productAnswerDTO2.setId(2L);
        assertThat(productAnswerDTO1).isNotEqualTo(productAnswerDTO2);
        productAnswerDTO1.setId(null);
        assertThat(productAnswerDTO1).isNotEqualTo(productAnswerDTO2);
    }
}
