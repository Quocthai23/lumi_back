package com.lumiere.app.domain;

import static com.lumiere.app.domain.AddressTestSamples.*;
import static com.lumiere.app.domain.CustomerTestSamples.*;
import static com.lumiere.app.domain.LoyaltyTransactionTestSamples.*;
import static com.lumiere.app.domain.NotificationTestSamples.*;
import static com.lumiere.app.domain.OrdersTestSamples.*;
import static com.lumiere.app.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void ordersTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        customer.addOrders(ordersBack);
        assertThat(customer.getOrders()).containsOnly(ordersBack);
        assertThat(ordersBack.getCustomer()).isEqualTo(customer);

        customer.removeOrders(ordersBack);
        assertThat(customer.getOrders()).doesNotContain(ordersBack);
        assertThat(ordersBack.getCustomer()).isNull();

        customer.orders(new HashSet<>(Set.of(ordersBack)));
        assertThat(customer.getOrders()).containsOnly(ordersBack);
        assertThat(ordersBack.getCustomer()).isEqualTo(customer);

        customer.setOrders(new HashSet<>());
        assertThat(customer.getOrders()).doesNotContain(ordersBack);
        assertThat(ordersBack.getCustomer()).isNull();
    }

    @Test
    void wishlistTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        customer.addWishlist(productBack);
        assertThat(customer.getWishlists()).containsOnly(productBack);

        customer.removeWishlist(productBack);
        assertThat(customer.getWishlists()).doesNotContain(productBack);

        customer.wishlists(new HashSet<>(Set.of(productBack)));
        assertThat(customer.getWishlists()).containsOnly(productBack);

        customer.setWishlists(new HashSet<>());
        assertThat(customer.getWishlists()).doesNotContain(productBack);
    }

    @Test
    void addressesTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        customer.addAddresses(addressBack);
        assertThat(customer.getAddresses()).containsOnly(addressBack);
        assertThat(addressBack.getCustomer()).isEqualTo(customer);

        customer.removeAddresses(addressBack);
        assertThat(customer.getAddresses()).doesNotContain(addressBack);
        assertThat(addressBack.getCustomer()).isNull();

        customer.addresses(new HashSet<>(Set.of(addressBack)));
        assertThat(customer.getAddresses()).containsOnly(addressBack);
        assertThat(addressBack.getCustomer()).isEqualTo(customer);

        customer.setAddresses(new HashSet<>());
        assertThat(customer.getAddresses()).doesNotContain(addressBack);
        assertThat(addressBack.getCustomer()).isNull();
    }

    @Test
    void loyaltyHistoryTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        LoyaltyTransaction loyaltyTransactionBack = getLoyaltyTransactionRandomSampleGenerator();

        customer.addLoyaltyHistory(loyaltyTransactionBack);
        assertThat(customer.getLoyaltyHistories()).containsOnly(loyaltyTransactionBack);
        assertThat(loyaltyTransactionBack.getCustomer()).isEqualTo(customer);

        customer.removeLoyaltyHistory(loyaltyTransactionBack);
        assertThat(customer.getLoyaltyHistories()).doesNotContain(loyaltyTransactionBack);
        assertThat(loyaltyTransactionBack.getCustomer()).isNull();

        customer.loyaltyHistories(new HashSet<>(Set.of(loyaltyTransactionBack)));
        assertThat(customer.getLoyaltyHistories()).containsOnly(loyaltyTransactionBack);
        assertThat(loyaltyTransactionBack.getCustomer()).isEqualTo(customer);

        customer.setLoyaltyHistories(new HashSet<>());
        assertThat(customer.getLoyaltyHistories()).doesNotContain(loyaltyTransactionBack);
        assertThat(loyaltyTransactionBack.getCustomer()).isNull();
    }

    @Test
    void notificationsTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        customer.addNotifications(notificationBack);
        assertThat(customer.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getCustomer()).isEqualTo(customer);

        customer.removeNotifications(notificationBack);
        assertThat(customer.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getCustomer()).isNull();

        customer.notifications(new HashSet<>(Set.of(notificationBack)));
        assertThat(customer.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getCustomer()).isEqualTo(customer);

        customer.setNotifications(new HashSet<>());
        assertThat(customer.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getCustomer()).isNull();
    }
}
