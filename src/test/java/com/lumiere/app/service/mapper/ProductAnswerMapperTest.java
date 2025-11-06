package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.ProductAnswerAsserts.*;
import static com.lumiere.app.domain.ProductAnswerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductAnswerMapperTest {

    private ProductAnswerMapper productAnswerMapper;

    @BeforeEach
    void setUp() {
        productAnswerMapper = new ProductAnswerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductAnswerSample1();
        var actual = productAnswerMapper.toEntity(productAnswerMapper.toDto(expected));
        assertProductAnswerAllPropertiesEquals(expected, actual);
    }
}
