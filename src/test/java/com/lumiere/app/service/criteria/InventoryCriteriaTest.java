package com.lumiere.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InventoryCriteriaTest {

    @Test
    void newInventoryCriteriaHasAllFiltersNullTest() {
        var inventoryCriteria = new InventoryCriteria();
        assertThat(inventoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void inventoryCriteriaFluentMethodsCreatesFiltersTest() {
        var inventoryCriteria = new InventoryCriteria();

        setAllFilters(inventoryCriteria);

        assertThat(inventoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void inventoryCriteriaCopyCreatesNullFilterTest() {
        var inventoryCriteria = new InventoryCriteria();
        var copy = inventoryCriteria.copy();

        assertThat(inventoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(inventoryCriteria)
        );
    }

    @Test
    void inventoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var inventoryCriteria = new InventoryCriteria();
        setAllFilters(inventoryCriteria);

        var copy = inventoryCriteria.copy();

        assertThat(inventoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(inventoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var inventoryCriteria = new InventoryCriteria();

        assertThat(inventoryCriteria).hasToString("InventoryCriteria{}");
    }

    private static void setAllFilters(InventoryCriteria inventoryCriteria) {
        inventoryCriteria.id();
        inventoryCriteria.stockQuantity();
        inventoryCriteria.productVariantId();
        inventoryCriteria.warehouseId();
        inventoryCriteria.distinct();
    }

    private static Condition<InventoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStockQuantity()) &&
                condition.apply(criteria.getProductVariantId()) &&
                condition.apply(criteria.getWarehouseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InventoryCriteria> copyFiltersAre(InventoryCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStockQuantity(), copy.getStockQuantity()) &&
                condition.apply(criteria.getProductVariantId(), copy.getProductVariantId()) &&
                condition.apply(criteria.getWarehouseId(), copy.getWarehouseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
