package com.lumiere.app.domain;

import static com.lumiere.app.domain.ProductAnswerTestSamples.*;
import static com.lumiere.app.domain.ProductQuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductAnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductAnswer.class);
        ProductAnswer productAnswer1 = getProductAnswerSample1();
        ProductAnswer productAnswer2 = new ProductAnswer();
        assertThat(productAnswer1).isNotEqualTo(productAnswer2);

        productAnswer2.setId(productAnswer1.getId());
        assertThat(productAnswer1).isEqualTo(productAnswer2);

        productAnswer2 = getProductAnswerSample2();
        assertThat(productAnswer1).isNotEqualTo(productAnswer2);
    }

    @Test
    void questionTest() {
        ProductAnswer productAnswer = getProductAnswerRandomSampleGenerator();
        ProductQuestion productQuestionBack = getProductQuestionRandomSampleGenerator();

        productAnswer.setQuestion(productQuestionBack);
        assertThat(productAnswer.getQuestion()).isEqualTo(productQuestionBack);

        productAnswer.question(null);
        assertThat(productAnswer.getQuestion()).isNull();
    }
}
