package com.lumiere.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WarehouseCriteriaTest {

    @Test
    void newWarehouseCriteriaHasAllFiltersNullTest() {
        var warehouseCriteria = new WarehouseCriteria();
        assertThat(warehouseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void warehouseCriteriaFluentMethodsCreatesFiltersTest() {
        var warehouseCriteria = new WarehouseCriteria();

        setAllFilters(warehouseCriteria);

        assertThat(warehouseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void warehouseCriteriaCopyCreatesNullFilterTest() {
        var warehouseCriteria = new WarehouseCriteria();
        var copy = warehouseCriteria.copy();

        assertThat(warehouseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(warehouseCriteria)
        );
    }

    @Test
    void warehouseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var warehouseCriteria = new WarehouseCriteria();
        setAllFilters(warehouseCriteria);

        var copy = warehouseCriteria.copy();

        assertThat(warehouseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(warehouseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var warehouseCriteria = new WarehouseCriteria();

        assertThat(warehouseCriteria).hasToString("WarehouseCriteria{}");
    }

    private static void setAllFilters(WarehouseCriteria warehouseCriteria) {
        warehouseCriteria.id();
        warehouseCriteria.name();
        warehouseCriteria.address();
        warehouseCriteria.isActive();
        warehouseCriteria.distinct();
    }

    private static Condition<WarehouseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WarehouseCriteria> copyFiltersAre(WarehouseCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
