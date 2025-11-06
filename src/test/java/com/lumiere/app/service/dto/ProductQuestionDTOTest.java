package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductQuestionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductQuestionDTO.class);
        ProductQuestionDTO productQuestionDTO1 = new ProductQuestionDTO();
        productQuestionDTO1.setId(1L);
        ProductQuestionDTO productQuestionDTO2 = new ProductQuestionDTO();
        assertThat(productQuestionDTO1).isNotEqualTo(productQuestionDTO2);
        productQuestionDTO2.setId(productQuestionDTO1.getId());
        assertThat(productQuestionDTO1).isEqualTo(productQuestionDTO2);
        productQuestionDTO2.setId(2L);
        assertThat(productQuestionDTO1).isNotEqualTo(productQuestionDTO2);
        productQuestionDTO1.setId(null);
        assertThat(productQuestionDTO1).isNotEqualTo(productQuestionDTO2);
    }
}
