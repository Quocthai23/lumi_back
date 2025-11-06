package com.lumiere.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrdersCriteriaTest {

    @Test
    void newOrdersCriteriaHasAllFiltersNullTest() {
        var ordersCriteria = new OrdersCriteria();
        assertThat(ordersCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ordersCriteriaFluentMethodsCreatesFiltersTest() {
        var ordersCriteria = new OrdersCriteria();

        setAllFilters(ordersCriteria);

        assertThat(ordersCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ordersCriteriaCopyCreatesNullFilterTest() {
        var ordersCriteria = new OrdersCriteria();
        var copy = ordersCriteria.copy();

        assertThat(ordersCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ordersCriteria)
        );
    }

    @Test
    void ordersCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ordersCriteria = new OrdersCriteria();
        setAllFilters(ordersCriteria);

        var copy = ordersCriteria.copy();

        assertThat(ordersCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ordersCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ordersCriteria = new OrdersCriteria();

        assertThat(ordersCriteria).hasToString("OrdersCriteria{}");
    }

    private static void setAllFilters(OrdersCriteria ordersCriteria) {
        ordersCriteria.id();
        ordersCriteria.code();
        ordersCriteria.status();
        ordersCriteria.paymentStatus();
        ordersCriteria.totalAmount();
        ordersCriteria.note();
        ordersCriteria.paymentMethod();
        ordersCriteria.placedAt();
        ordersCriteria.redeemedPoints();
        ordersCriteria.customerId();
        ordersCriteria.orderItemsId();
        ordersCriteria.orderStatusHistoryId();
        ordersCriteria.distinct();
    }

    private static Condition<OrdersCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPaymentStatus()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getPaymentMethod()) &&
                condition.apply(criteria.getPlacedAt()) &&
                condition.apply(criteria.getRedeemedPoints()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getOrderItemsId()) &&
                condition.apply(criteria.getOrderStatusHistoryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrdersCriteria> copyFiltersAre(OrdersCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPaymentStatus(), copy.getPaymentStatus()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getPaymentMethod(), copy.getPaymentMethod()) &&
                condition.apply(criteria.getPlacedAt(), copy.getPlacedAt()) &&
                condition.apply(criteria.getRedeemedPoints(), copy.getRedeemedPoints()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getOrderItemsId(), copy.getOrderItemsId()) &&
                condition.apply(criteria.getOrderStatusHistoryId(), copy.getOrderStatusHistoryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
