package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.FlashSaleProductAsserts.*;
import static com.lumiere.app.domain.FlashSaleProductTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlashSaleProductMapperTest {

    private FlashSaleProductMapper flashSaleProductMapper;

    @BeforeEach
    void setUp() {
        flashSaleProductMapper = new FlashSaleProductMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFlashSaleProductSample1();
        var actual = flashSaleProductMapper.toEntity(flashSaleProductMapper.toDto(expected));
        assertFlashSaleProductAllPropertiesEquals(expected, actual);
    }
}
