package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.StockMovementAsserts.*;
import static com.lumiere.app.domain.StockMovementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockMovementMapperTest {

    private StockMovementMapper stockMovementMapper;

    @BeforeEach
    void setUp() {
        stockMovementMapper = new StockMovementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockMovementSample1();
        var actual = stockMovementMapper.toEntity(stockMovementMapper.toDto(expected));
        assertStockMovementAllPropertiesEquals(expected, actual);
    }
}
