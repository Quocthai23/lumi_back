package com.lumiere.app.domain;

import static com.lumiere.app.domain.ProductAnswerTestSamples.*;
import static com.lumiere.app.domain.ProductQuestionTestSamples.*;
import static com.lumiere.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductQuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductQuestion.class);
        ProductQuestion productQuestion1 = getProductQuestionSample1();
        ProductQuestion productQuestion2 = new ProductQuestion();
        assertThat(productQuestion1).isNotEqualTo(productQuestion2);

        productQuestion2.setId(productQuestion1.getId());
        assertThat(productQuestion1).isEqualTo(productQuestion2);

        productQuestion2 = getProductQuestionSample2();
        assertThat(productQuestion1).isNotEqualTo(productQuestion2);
    }

    @Test
    void productTest() {
        ProductQuestion productQuestion = getProductQuestionRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productQuestion.setProduct(productBack);
        assertThat(productQuestion.getProduct()).isEqualTo(productBack);

        productQuestion.product(null);
        assertThat(productQuestion.getProduct()).isNull();
    }

    @Test
    void answersTest() {
        ProductQuestion productQuestion = getProductQuestionRandomSampleGenerator();
        ProductAnswer productAnswerBack = getProductAnswerRandomSampleGenerator();

        productQuestion.addAnswers(productAnswerBack);
        assertThat(productQuestion.getAnswers()).containsOnly(productAnswerBack);
        assertThat(productAnswerBack.getQuestion()).isEqualTo(productQuestion);

        productQuestion.removeAnswers(productAnswerBack);
        assertThat(productQuestion.getAnswers()).doesNotContain(productAnswerBack);
        assertThat(productAnswerBack.getQuestion()).isNull();

        productQuestion.answers(new HashSet<>(Set.of(productAnswerBack)));
        assertThat(productQuestion.getAnswers()).containsOnly(productAnswerBack);
        assertThat(productAnswerBack.getQuestion()).isEqualTo(productQuestion);

        productQuestion.setAnswers(new HashSet<>());
        assertThat(productQuestion.getAnswers()).doesNotContain(productAnswerBack);
        assertThat(productAnswerBack.getQuestion()).isNull();
    }
}
