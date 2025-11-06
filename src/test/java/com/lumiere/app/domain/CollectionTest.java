package com.lumiere.app.domain;

import static com.lumiere.app.domain.CollectionTestSamples.*;
import static com.lumiere.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Collection.class);
        Collection collection1 = getCollectionSample1();
        Collection collection2 = new Collection();
        assertThat(collection1).isNotEqualTo(collection2);

        collection2.setId(collection1.getId());
        assertThat(collection1).isEqualTo(collection2);

        collection2 = getCollectionSample2();
        assertThat(collection1).isNotEqualTo(collection2);
    }

    @Test
    void productsTest() {
        Collection collection = getCollectionRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        collection.addProducts(productBack);
        assertThat(collection.getProducts()).containsOnly(productBack);

        collection.removeProducts(productBack);
        assertThat(collection.getProducts()).doesNotContain(productBack);

        collection.products(new HashSet<>(Set.of(productBack)));
        assertThat(collection.getProducts()).containsOnly(productBack);

        collection.setProducts(new HashSet<>());
        assertThat(collection.getProducts()).doesNotContain(productBack);
    }
}
