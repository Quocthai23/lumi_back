package com.lumiere.app.domain;

import static com.lumiere.app.domain.CollectionTestSamples.*;
import static com.lumiere.app.domain.CustomerTestSamples.*;
import static com.lumiere.app.domain.ProductQuestionTestSamples.*;
import static com.lumiere.app.domain.ProductReviewTestSamples.*;
import static com.lumiere.app.domain.ProductTestSamples.*;
import static com.lumiere.app.domain.ProductVariantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void variantsTest() {
        Product product = getProductRandomSampleGenerator();
        ProductVariant productVariantBack = getProductVariantRandomSampleGenerator();

        product.addVariants(productVariantBack);
        assertThat(product.getVariants()).containsOnly(productVariantBack);
        assertThat(productVariantBack.getProduct()).isEqualTo(product);

        product.removeVariants(productVariantBack);
        assertThat(product.getVariants()).doesNotContain(productVariantBack);
        assertThat(productVariantBack.getProduct()).isNull();

        product.variants(new HashSet<>(Set.of(productVariantBack)));
        assertThat(product.getVariants()).containsOnly(productVariantBack);
        assertThat(productVariantBack.getProduct()).isEqualTo(product);

        product.setVariants(new HashSet<>());
        assertThat(product.getVariants()).doesNotContain(productVariantBack);
        assertThat(productVariantBack.getProduct()).isNull();
    }

    @Test
    void reviewsTest() {
        Product product = getProductRandomSampleGenerator();
        ProductReview productReviewBack = getProductReviewRandomSampleGenerator();

        product.addReviews(productReviewBack);
        assertThat(product.getReviews()).containsOnly(productReviewBack);
        assertThat(productReviewBack.getProduct()).isEqualTo(product);

        product.removeReviews(productReviewBack);
        assertThat(product.getReviews()).doesNotContain(productReviewBack);
        assertThat(productReviewBack.getProduct()).isNull();

        product.reviews(new HashSet<>(Set.of(productReviewBack)));
        assertThat(product.getReviews()).containsOnly(productReviewBack);
        assertThat(productReviewBack.getProduct()).isEqualTo(product);

        product.setReviews(new HashSet<>());
        assertThat(product.getReviews()).doesNotContain(productReviewBack);
        assertThat(productReviewBack.getProduct()).isNull();
    }

    @Test
    void questionsTest() {
        Product product = getProductRandomSampleGenerator();
        ProductQuestion productQuestionBack = getProductQuestionRandomSampleGenerator();

        product.addQuestions(productQuestionBack);
        assertThat(product.getQuestions()).containsOnly(productQuestionBack);
        assertThat(productQuestionBack.getProduct()).isEqualTo(product);

        product.removeQuestions(productQuestionBack);
        assertThat(product.getQuestions()).doesNotContain(productQuestionBack);
        assertThat(productQuestionBack.getProduct()).isNull();

        product.questions(new HashSet<>(Set.of(productQuestionBack)));
        assertThat(product.getQuestions()).containsOnly(productQuestionBack);
        assertThat(productQuestionBack.getProduct()).isEqualTo(product);

        product.setQuestions(new HashSet<>());
        assertThat(product.getQuestions()).doesNotContain(productQuestionBack);
        assertThat(productQuestionBack.getProduct()).isNull();
    }

    @Test
    void collectionsTest() {
        Product product = getProductRandomSampleGenerator();
        Collection collectionBack = getCollectionRandomSampleGenerator();

        product.addCollections(collectionBack);
        assertThat(product.getCollections()).containsOnly(collectionBack);
        assertThat(collectionBack.getProducts()).containsOnly(product);

        product.removeCollections(collectionBack);
        assertThat(product.getCollections()).doesNotContain(collectionBack);
        assertThat(collectionBack.getProducts()).doesNotContain(product);

        product.collections(new HashSet<>(Set.of(collectionBack)));
        assertThat(product.getCollections()).containsOnly(collectionBack);
        assertThat(collectionBack.getProducts()).containsOnly(product);

        product.setCollections(new HashSet<>());
        assertThat(product.getCollections()).doesNotContain(collectionBack);
        assertThat(collectionBack.getProducts()).doesNotContain(product);
    }

    @Test
    void wishlistedByTest() {
        Product product = getProductRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        product.addWishlistedBy(customerBack);
        assertThat(product.getWishlistedBies()).containsOnly(customerBack);
        assertThat(customerBack.getWishlists()).containsOnly(product);

        product.removeWishlistedBy(customerBack);
        assertThat(product.getWishlistedBies()).doesNotContain(customerBack);
        assertThat(customerBack.getWishlists()).doesNotContain(product);

        product.wishlistedBies(new HashSet<>(Set.of(customerBack)));
        assertThat(product.getWishlistedBies()).containsOnly(customerBack);
        assertThat(customerBack.getWishlists()).containsOnly(product);

        product.setWishlistedBies(new HashSet<>());
        assertThat(product.getWishlistedBies()).doesNotContain(customerBack);
        assertThat(customerBack.getWishlists()).doesNotContain(product);
    }
}
