package com.lumiere.app.domain;

import static com.lumiere.app.domain.OrderStatusHistoryTestSamples.*;
import static com.lumiere.app.domain.OrdersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderStatusHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusHistory.class);
        OrderStatusHistory orderStatusHistory1 = getOrderStatusHistorySample1();
        OrderStatusHistory orderStatusHistory2 = new OrderStatusHistory();
        assertThat(orderStatusHistory1).isNotEqualTo(orderStatusHistory2);

        orderStatusHistory2.setId(orderStatusHistory1.getId());
        assertThat(orderStatusHistory1).isEqualTo(orderStatusHistory2);

        orderStatusHistory2 = getOrderStatusHistorySample2();
        assertThat(orderStatusHistory1).isNotEqualTo(orderStatusHistory2);
    }

    @Test
    void orderTest() {
        OrderStatusHistory orderStatusHistory = getOrderStatusHistoryRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        orderStatusHistory.setOrder(ordersBack);
        assertThat(orderStatusHistory.getOrder()).isEqualTo(ordersBack);

        orderStatusHistory.order(null);
        assertThat(orderStatusHistory.getOrder()).isNull();
    }
}
