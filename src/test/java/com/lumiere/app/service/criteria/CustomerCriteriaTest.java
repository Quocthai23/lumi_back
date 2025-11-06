package com.lumiere.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CustomerCriteriaTest {

    @Test
    void newCustomerCriteriaHasAllFiltersNullTest() {
        var customerCriteria = new CustomerCriteria();
        assertThat(customerCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void customerCriteriaFluentMethodsCreatesFiltersTest() {
        var customerCriteria = new CustomerCriteria();

        setAllFilters(customerCriteria);

        assertThat(customerCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void customerCriteriaCopyCreatesNullFilterTest() {
        var customerCriteria = new CustomerCriteria();
        var copy = customerCriteria.copy();

        assertThat(customerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(customerCriteria)
        );
    }

    @Test
    void customerCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var customerCriteria = new CustomerCriteria();
        setAllFilters(customerCriteria);

        var copy = customerCriteria.copy();

        assertThat(customerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(customerCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var customerCriteria = new CustomerCriteria();

        assertThat(customerCriteria).hasToString("CustomerCriteria{}");
    }

    private static void setAllFilters(CustomerCriteria customerCriteria) {
        customerCriteria.id();
        customerCriteria.firstName();
        customerCriteria.lastName();
        customerCriteria.phone();
        customerCriteria.tier();
        customerCriteria.loyaltyPoints();
        customerCriteria.userId();
        customerCriteria.ordersId();
        customerCriteria.wishlistId();
        customerCriteria.addressesId();
        customerCriteria.loyaltyHistoryId();
        customerCriteria.notificationsId();
        customerCriteria.distinct();
    }

    private static Condition<CustomerCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getTier()) &&
                condition.apply(criteria.getLoyaltyPoints()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getOrdersId()) &&
                condition.apply(criteria.getWishlistId()) &&
                condition.apply(criteria.getAddressesId()) &&
                condition.apply(criteria.getLoyaltyHistoryId()) &&
                condition.apply(criteria.getNotificationsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CustomerCriteria> copyFiltersAre(CustomerCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getTier(), copy.getTier()) &&
                condition.apply(criteria.getLoyaltyPoints(), copy.getLoyaltyPoints()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getOrdersId(), copy.getOrdersId()) &&
                condition.apply(criteria.getWishlistId(), copy.getWishlistId()) &&
                condition.apply(criteria.getAddressesId(), copy.getAddressesId()) &&
                condition.apply(criteria.getLoyaltyHistoryId(), copy.getLoyaltyHistoryId()) &&
                condition.apply(criteria.getNotificationsId(), copy.getNotificationsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
