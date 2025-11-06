package com.lumiere.app.domain;

import static com.lumiere.app.domain.CustomerTestSamples.*;
import static com.lumiere.app.domain.OrderItemTestSamples.*;
import static com.lumiere.app.domain.OrderStatusHistoryTestSamples.*;
import static com.lumiere.app.domain.OrdersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrdersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Orders.class);
        Orders orders1 = getOrdersSample1();
        Orders orders2 = new Orders();
        assertThat(orders1).isNotEqualTo(orders2);

        orders2.setId(orders1.getId());
        assertThat(orders1).isEqualTo(orders2);

        orders2 = getOrdersSample2();
        assertThat(orders1).isNotEqualTo(orders2);
    }

    @Test
    void customerTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        orders.setCustomer(customerBack);
        assertThat(orders.getCustomer()).isEqualTo(customerBack);

        orders.customer(null);
        assertThat(orders.getCustomer()).isNull();
    }

    @Test
    void orderItemsTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        OrderItem orderItemBack = getOrderItemRandomSampleGenerator();

        orders.addOrderItems(orderItemBack);
        assertThat(orders.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(orders);

        orders.removeOrderItems(orderItemBack);
        assertThat(orders.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();

        orders.orderItems(new HashSet<>(Set.of(orderItemBack)));
        assertThat(orders.getOrderItems()).containsOnly(orderItemBack);
        assertThat(orderItemBack.getOrder()).isEqualTo(orders);

        orders.setOrderItems(new HashSet<>());
        assertThat(orders.getOrderItems()).doesNotContain(orderItemBack);
        assertThat(orderItemBack.getOrder()).isNull();
    }

    @Test
    void orderStatusHistoryTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        OrderStatusHistory orderStatusHistoryBack = getOrderStatusHistoryRandomSampleGenerator();

        orders.addOrderStatusHistory(orderStatusHistoryBack);
        assertThat(orders.getOrderStatusHistories()).containsOnly(orderStatusHistoryBack);
        assertThat(orderStatusHistoryBack.getOrder()).isEqualTo(orders);

        orders.removeOrderStatusHistory(orderStatusHistoryBack);
        assertThat(orders.getOrderStatusHistories()).doesNotContain(orderStatusHistoryBack);
        assertThat(orderStatusHistoryBack.getOrder()).isNull();

        orders.orderStatusHistories(new HashSet<>(Set.of(orderStatusHistoryBack)));
        assertThat(orders.getOrderStatusHistories()).containsOnly(orderStatusHistoryBack);
        assertThat(orderStatusHistoryBack.getOrder()).isEqualTo(orders);

        orders.setOrderStatusHistories(new HashSet<>());
        assertThat(orders.getOrderStatusHistories()).doesNotContain(orderStatusHistoryBack);
        assertThat(orderStatusHistoryBack.getOrder()).isNull();
    }
}
