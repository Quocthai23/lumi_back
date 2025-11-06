package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.FlashSaleAsserts.*;
import static com.lumiere.app.domain.FlashSaleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlashSaleMapperTest {

    private FlashSaleMapper flashSaleMapper;

    @BeforeEach
    void setUp() {
        flashSaleMapper = new FlashSaleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFlashSaleSample1();
        var actual = flashSaleMapper.toEntity(flashSaleMapper.toDto(expected));
        assertFlashSaleAllPropertiesEquals(expected, actual);
    }
}
