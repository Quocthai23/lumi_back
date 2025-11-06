package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.StockNotificationAsserts.*;
import static com.lumiere.app.domain.StockNotificationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockNotificationMapperTest {

    private StockNotificationMapper stockNotificationMapper;

    @BeforeEach
    void setUp() {
        stockNotificationMapper = new StockNotificationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockNotificationSample1();
        var actual = stockNotificationMapper.toEntity(stockNotificationMapper.toDto(expected));
        assertStockNotificationAllPropertiesEquals(expected, actual);
    }
}
