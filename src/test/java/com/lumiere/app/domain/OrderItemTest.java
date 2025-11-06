package com.lumiere.app.domain;

import static com.lumiere.app.domain.OrderItemTestSamples.*;
import static com.lumiere.app.domain.OrdersTestSamples.*;
import static com.lumiere.app.domain.ProductVariantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        orderItem.setOrder(ordersBack);
        assertThat(orderItem.getOrder()).isEqualTo(ordersBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }

    @Test
    void productVariantTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        ProductVariant productVariantBack = getProductVariantRandomSampleGenerator();

        orderItem.setProductVariant(productVariantBack);
        assertThat(orderItem.getProductVariant()).isEqualTo(productVariantBack);

        orderItem.productVariant(null);
        assertThat(orderItem.getProductVariant()).isNull();
    }
}
