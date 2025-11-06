package com.lumiere.app.domain;

import static com.lumiere.app.domain.CustomerTestSamples.*;
import static com.lumiere.app.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void customerTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        notification.setCustomer(customerBack);
        assertThat(notification.getCustomer()).isEqualTo(customerBack);

        notification.customer(null);
        assertThat(notification.getCustomer()).isNull();
    }
}
